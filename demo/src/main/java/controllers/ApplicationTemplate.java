/*
 * Copyright 2015 Fizzed Inc.
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
package controllers;

import com.fizzed.ninja.rocker.NinjaRockerTemplate;
import com.fizzed.rocker.RockerModel;
import com.fizzed.rocker.RockerTemplate;

/**
 *
 * @author joelauer
 */
public abstract class ApplicationTemplate extends NinjaRockerTemplate {

    public String requestMethod;
    
    public ApplicationTemplate(RockerModel model) {
        super(model);
    }
    
    @Override
    protected void __associate(RockerTemplate context) {
        super.__associate(context);
        
        if (context instanceof ApplicationTemplate) {
            ApplicationTemplate applicationContext = (ApplicationTemplate)context;
            this.requestMethod = applicationContext.requestMethod;
        }
        else {
            throw new IllegalArgumentException("Unable to associate (context was not an instance of " + ApplicationTemplate.class.getCanonicalName() + ")");
        }
    }
    
}
