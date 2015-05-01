/*
 * Copyright 2014-2015 Nikos Grammatikos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://raw.githubusercontent.com/nikosgram13/OglofusProtection/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.nikosgram.oglofus.configuration;

public interface StorageDriver< T >
{
    /**
     * Create the configuration file and the parent directories.
     *
     * @return true if the configuration file exists!
     */
    boolean create();

    /**
     * Write the object to the file.
     */
    void save();

    /**
     * Reading and parse the configuration file in a stream and
     * produce the corresponding Java object.
     */
    void load();
}
