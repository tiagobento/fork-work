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

import { JavaWrapper } from "../JavaWrapper";

describe("extendsJavaWrapper", () => {
  test("with class extending java wrapper, should return true", () => {
    const input = new MyNumberType(1);

    const output = JavaWrapper.extendsJavaWrapper(input);

    expect(output).toBeTruthy();
  });

  test("with class not extending java wrapper, should return false", () => {
    const input = {
      foo: "bar"
    };

    const output = JavaWrapper.extendsJavaWrapper(input);

    expect(output).toBeFalsy();
  });

  class MyNumberType extends JavaWrapper<number> {
    private _value: number;

    constructor(value: number) {
      super();
      this._value = value;
    }

    public get(): number {
      return this._value;
    }

    public set(val: ((current: number) => number) | number): void {
      // not used
    }
  }
});
