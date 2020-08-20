/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit.util.commands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.uberfire.java.nio.base.TextualDiff;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class TextualDiffBranches {

    private final Git git;
    private final String branchA;
    private final String branchB;
    private final String commitIdBranchA;
    private final String commitIdBranchB;

    private static final String DIFF_REGEX_DELIMITER = "diff --git.*";
    private static final String DIFF_KEY = "diff --git a/%s b/%s";

    public TextualDiffBranches(final Git git,
                               final String branchA,
                               final String branchB) {
        this(git,
             branchA,
             branchB,
             null,
             null);
    }

    public TextualDiffBranches(final Git git,
                               final String branchA,
                               final String branchB,
                               final String commitIdBranchA,
                               final String commitIdBranchB) {
        this.git = checkNotNull("git",
                                git);
        this.branchA = checkNotEmpty("branchA",
                                     branchA);
        this.branchB = checkNotEmpty("branchB",
                                     branchB);

        this.commitIdBranchA = commitIdBranchA;
        this.commitIdBranchB = commitIdBranchB;
    }

    public List<TextualDiff> execute() {
        final DiffFormatter formatter = createFormatter();

        BranchUtil.existsBranch(this.git,
                                this.branchA);
        BranchUtil.existsBranch(this.git,
                                this.branchB);

        try (final ObjectReader reader = git.getRepository().newObjectReader()) {

            final RevCommit commitA = this.commitIdBranchA != null ?
                    git.getCommit(commitIdBranchA) :
                    git.getCommonAncestorCommit(branchA,
                                                branchB);

            final RevCommit commitB = this.commitIdBranchB != null ?
                    git.getCommit(commitIdBranchB) :
                    git.getLastCommit(branchB);

            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader,
                              commitA.getTree());

            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader,
                              commitB.getTree());

            OutputStream out = new ByteArrayOutputStream();
            List<DiffEntry> diffEntries = new CustomDiffCommand(git)
                    .setNewTree(newTreeIter)
                    .setOldTree(oldTreeIter)
                    .setOutputStream(out)
                    .call();

            List<String> parts = TextualDiffBranches.splitWithDelimiters(String.valueOf(out),
                                                                         DIFF_REGEX_DELIMITER);

            Map<String, String> diffMap = new HashMap<>();
            for (int i = 1, j = 0; i < parts.size(); i += 2, j++) {
                String diffKey = buildDiffKey(diffEntries.get(j).getChangeType(),
                                              diffEntries.get(j).getOldPath(),
                                              diffEntries.get(j).getNewPath());

                diffMap.put(diffKey, parts.get(i));
            }

            return diffEntries.stream()
                    .map(entry -> getFileHeader(formatter, entry))
                    .map(header -> {
                        int linesAdded = header.toEditList()
                                .stream().mapToInt(elem -> elem.getEndB() - elem.getBeginB()).sum();

                        int linesDeleted = header.toEditList()
                                .stream().mapToInt(elem -> elem.getEndA() - elem.getBeginA()).sum();

                        DiffEntry.ChangeType changeType = header.getChangeType();

                        String diffKey = buildDiffKey(changeType,
                                                      header.getOldPath(),
                                                      header.getNewPath());

                        String diffText = diffKey + diffMap.get(diffKey);

                        return new TextualDiff(header.getOldPath(),
                                               header.getNewPath(),
                                               changeType.toString(),
                                               linesAdded,
                                               linesDeleted,
                                               diffText);
                    }).collect(Collectors.toList());
        } catch (final Exception e) {
            throw new GitException("Unable to get textual diff", e);
        }
    }

    private String buildDiffKey(final DiffEntry.ChangeType changeType,
                                final String oldPath,
                                final String newPath) {
        return String.format(DIFF_KEY,
                             changeType != DiffEntry.ChangeType.ADD ? oldPath : newPath,
                             changeType != DiffEntry.ChangeType.DELETE ? newPath : oldPath);
    }

    private DiffFormatter createFormatter() {
        OutputStream outputStream = new ByteArrayOutputStream();
        DiffFormatter formatter = new DiffFormatter(outputStream);
        formatter.setRepository(git.getRepository());
        return formatter;
    }

    private FileHeader getFileHeader(final DiffFormatter formatter,
                                     final DiffEntry elem) {
        try {
            return formatter.toFileHeader(elem);
        } catch (IOException e) {
            throw new GitException("A problem occurred when trying to obtain diffs between files",
                                   e);
        }
    }

    private static List<String> splitWithDelimiters(String str, String regex) {
        List<String> parts = new ArrayList<>();

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);

        int lastEnd = 0;
        while (m.find()) {
            int start = m.start();
            if (lastEnd != start) {
                String nonDelim = str.substring(lastEnd, start);
                parts.add(nonDelim);
            }
            String delim = m.group();
            parts.add(delim);

            lastEnd = m.end();
        }

        if (lastEnd != str.length()) {
            String nonDelim = str.substring(lastEnd);
            parts.add(nonDelim);
        }

        return parts;
    }
}
