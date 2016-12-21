/*
 * Copyright 2016 Dominik Schlosser.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dmn1k.supercsv.io.declarative.annotation;

/**
 *
 * @author Dominik Schlosser
 */
public enum CsvMappingModeType {
    /**
     * Causes CsvDeclarativeBeanReader and CsvDeclarativeBeanWriter to throw an exception if explicit and implicit
     * column mapping are mixed and if the number of mapped fields does not match the number for cells in the csv-file
     */
    STRICT,
    /**
     * Uses explicit over implicit field mapping and ignores unmapped cells when reading
     */
    LOOSE;
}
