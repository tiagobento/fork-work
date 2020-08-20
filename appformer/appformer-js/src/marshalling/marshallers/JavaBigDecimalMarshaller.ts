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

import { NullableMarshaller } from "./NullableMarshaller";
import { JavaBigDecimal } from "../../java-wrappers/JavaBigDecimal";
import { ErraiObject } from "../model/ErraiObject";
import { MarshallingContext } from "../MarshallingContext";
import { UnmarshallingContext } from "../UnmarshallingContext";
import { ValueBasedErraiObject } from "../model/ValueBasedErraiObject";
import { NumberUtils } from "../../util/NumberUtils";
import { isString } from "../../util/TypeUtils";

export class JavaBigDecimalMarshaller extends NullableMarshaller<
  JavaBigDecimal,
  ErraiObject,
  ErraiObject,
  JavaBigDecimal
> {
  public notNullMarshall(input: JavaBigDecimal, ctx: MarshallingContext): ErraiObject {
    const fqcn = (input as any)._fqcn;
    const value = input.get().toString(10);
    const objectId = ctx.incrementAndGetObjectId().toString(10);
    return new ValueBasedErraiObject(fqcn, value, objectId).asErraiObject();
  }

  public notNullUnmarshall(input: ErraiObject, ctx: UnmarshallingContext): JavaBigDecimal {
    const valueFromJson = ValueBasedErraiObject.from(input).value as string;

    if (!JavaBigDecimalMarshaller.isValid(valueFromJson)) {
      throw new Error(`Invalid BigDecimal value ${valueFromJson}. Can't unmarshall json ${input}`);
    }

    return new JavaBigDecimal(valueFromJson);
  }

  private static isValid(valueFromJson: string): boolean {
    if (!valueFromJson) {
      return false;
    }

    if (!isString(valueFromJson)) {
      return false;
    }

    return NumberUtils.isFloatString(valueFromJson);
  }
}
