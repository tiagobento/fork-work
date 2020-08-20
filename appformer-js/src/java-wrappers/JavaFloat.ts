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

import { FloatBasedJavaNumber } from "./FloatBasedJavaNumber";
import { JavaByte } from "./JavaByte";
import { asByte, asDouble, asFloat, asInteger, asLong, asShort, JavaNumber } from "./JavaNumber";
import { JavaDouble } from "./JavaDouble";
import { JavaInteger } from "./JavaInteger";
import { JavaShort } from "./JavaShort";
import { JavaLong } from "./JavaLong";
import { JavaType } from "./JavaType";

export class JavaFloat extends FloatBasedJavaNumber implements JavaNumber {
  public static readonly MIN_VALUE = -3.40282347e38;
  public static readonly MAX_VALUE = 3.40282347e38;

  private readonly _fqcn = JavaType.FLOAT;

  protected isInRange(n: number): boolean {
    return n >= JavaFloat.MIN_VALUE && n <= JavaFloat.MAX_VALUE;
  }

  public byteValue(): JavaByte {
    return asByte(super.get());
  }

  public doubleValue(): JavaDouble {
    return asDouble(super.get());
  }

  public floatValue(): JavaFloat {
    return asFloat(super.get());
  }

  public intValue(): JavaInteger {
    return asInteger(super.get());
  }

  public shortValue(): JavaShort {
    return asShort(super.get());
  }

  public longValue(): JavaLong {
    return asLong(super.get());
  }
}
