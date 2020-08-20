/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ErraiObject } from "./ErraiObject";
import { ErraiObjectConstants } from "./ErraiObjectConstants";

export class NumValBasedErraiObject {
  public readonly encodedType: string;
  public readonly objId: string;
  public readonly numVal: number | boolean | string;

  constructor(encodedType: string, numVal: number | boolean | string, objectId: string = "-1") {
    this.encodedType = encodedType;
    this.numVal = numVal;
    this.objId = objectId;
  }

  public asErraiObject(): ErraiObject {
    return {
      [ErraiObjectConstants.ENCODED_TYPE]: this.encodedType,
      [ErraiObjectConstants.OBJECT_ID]: this.objId,
      [ErraiObjectConstants.NUM_VAL]: this.numVal
    };
  }

  public static from(obj: ErraiObject): NumValBasedErraiObject {
    return new NumValBasedErraiObject(
      obj[ErraiObjectConstants.ENCODED_TYPE],
      obj[ErraiObjectConstants.NUM_VAL]!,
      obj[ErraiObjectConstants.OBJECT_ID]
    );
  }
}
