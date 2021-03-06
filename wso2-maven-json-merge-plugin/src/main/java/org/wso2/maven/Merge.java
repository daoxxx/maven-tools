/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Mojo for Json Merge
 */
@Mojo(name = "merge")
public class Merge extends AbstractMojo {

    /**
     * List of tasks to merge
     */
    @Parameter
    private List<Task> tasks;

    public void execute() throws MojoExecutionException, MojoFailureException {
        for (Task taskModel : tasks) {
            Map baseJsonMap;
            if (taskModel.getBase() == null || taskModel.getBase().isEmpty()) {
                baseJsonMap = Collections.emptyMap();
            } else {
                baseJsonMap = Utils.getReadMap(taskModel.getBase());
            }
            Map inputMap;
            String targetPath = taskModel.getTarget();
            for (String aFile : taskModel.getInclude()) {
                inputMap = Utils.getReadMap(aFile);
                baseJsonMap = Utils.mergeMaps(baseJsonMap, inputMap, taskModel.isMergeChildren());
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(targetPath))) {
                bufferedWriter.write(Utils.convertIntoJson(baseJsonMap));
                bufferedWriter.flush();
            } catch (IOException e) {
                throw new MojoFailureException(e, "Error while Writing Merged Json", "Error while writing Json");
            }
        }

    }
}
