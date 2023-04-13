/*
 * # cangling.com . zengxx
 * Copyright (C) 2021 - 2023 . All Rights Reserved.
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
 * limitations under the License..
 */
package io.game.bootstrap.annotation;

import java.lang.annotation.*;

/**
 * 方法注解, 一般用作类方法的路由
 * <pre>
 *     方法 subCmd 注解
 *     一般用作类方法的路由
 * </pre>
 *
 * @author zengxx
 * @date 2021-12-12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Api {
    /**
     * cmd
     */
    int value();

    int ack() default 0;
}
