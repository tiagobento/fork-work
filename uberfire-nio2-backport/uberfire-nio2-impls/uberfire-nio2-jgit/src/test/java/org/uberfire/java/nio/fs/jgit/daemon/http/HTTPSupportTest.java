package org.uberfire.java.nio.fs.jgit.daemon.http;

import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRegistration;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.uberfire.java.nio.fs.jgit.AbstractTestInfra;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HTTPSupportTest extends AbstractTestInfra {

    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = super.getGitPreferences();
        gitPrefs.put(JGitFileSystemProviderConfiguration.GIT_HTTP_ENABLED, "true");
        return gitPrefs;
    }

    @Test
    public void testRoot() {
        base("");
        assertThat(provider.getFullHostNames().get("http")).isEqualTo("localhost:8080/git");
    }

    @Test
    public void testContext() {
        base("/app-former");
        assertThat(provider.getFullHostNames().get("http")).isEqualTo("localhost:8080/app-former/git");
    }

    public void base(final String contextPath) {
        final HTTPSupport httpSupport = new HTTPSupport() {
            @Override
            protected JGitFileSystemProvider resolveProvider() {
                return provider;
            }
        };

        final ServletContextEvent sce = mock(ServletContextEvent.class);

        final ServletContext sc = mock(ServletContext.class);
        final ServletRegistration.Dynamic dyn = mock(ServletRegistration.Dynamic.class);

        ArgumentCaptor<Servlet> servletArgumentCaptor = ArgumentCaptor.forClass(Servlet.class);

        when(sc.addServlet(anyString(), servletArgumentCaptor.capture())).thenReturn(dyn);

        when(sce.getServletContext()).thenReturn(sc);
        when(sc.getContextPath()).thenReturn(contextPath);

        httpSupport.contextInitialized(sce);

        verify(sc, times(1)).addServlet(anyString(), any(Servlet.class));
        verify(dyn, times(1)).addMapping("/git/*");
        verify(dyn, times(1)).setLoadOnStartup(1);
        verify(dyn, times(1)).setAsyncSupported(false);
    }
}
