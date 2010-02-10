/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.ant.props;

import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.taskdefs.condition.Condition;

/**
 * Property evaluator that evaluates Ant conditions to a Boolean
 * instance matching the condition's outcome.
 *
 * <p>Default syntax is
 * <code><em>condition</em>(<em>attribute</em>=<em>value</em>)</code>,
 * for example <code>os(family=unix)</code>.
 */
public class ConditionEvaluator extends RegexBasedEvaluator {
    public ConditionEvaluator() {
        super("^(.+?)\\(((?:(?:.+?)=(?:.+?))?(?:,(?:.+?)=(?:.+?))*?)\\)$");
    }

    protected Object evaluate(String[] groups, PropertyHelper propertyHelper) {
        Project p = propertyHelper.getProject();
        Object instance = ComponentHelper.getComponentHelper(p)
            .createComponent(groups[1]);
        if (instance instanceof Condition) {
            Condition cond = (Condition) instance;
            if (groups[2].length() > 0) {
                IntrospectionHelper ih =
                    IntrospectionHelper.getHelper(instance.getClass());
                String[] attributes = groups[2].split(",");
                for (int i = 0; i < attributes.length; i++) {
                    String[] keyValue = attributes[i].split("=");
                    ih.setAttribute(p, instance, keyValue[0], keyValue[1]);
                }
            }
            return Boolean.valueOf(cond.eval());
        }
        return null;
    }
}