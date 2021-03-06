/*
 * Copyright 2016 Fizzed, Inc.
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
package com.fizzed.ninja.rocker;

import com.fizzed.rocker.RockerModel;
import com.google.common.base.Optional;
import com.google.inject.Provider;
import custom.User;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import ninja.Context;
import ninja.Result;
import ninja.Route;
import ninja.Router;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.template.TemplateEngineHelper;
import ninja.utils.Message;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.ocpsoft.prettytime.PrettyTime;

public class TemplateEngineRockerTest {
    
    @Mock
    private Router router;
    @Mock
    private Messages messages;
    @Mock
    private NinjaProperties ninjaProperties;
    @Mock
    private Provider<Lang> langProvider;
    @Mock
    private Lang lang;
    @Mock
    private TemplateEngineHelper templateEngineHelper;
    @Mock
    private Context context;
    @Mock
    private Result result;
    private TemplateEngineRocker templateEngine;
    private ByteArrayOutputStream output;
    
    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        when(context.getContextPath()).thenReturn("");
        when(lang.getLanguage(context, Optional.of(result))).thenReturn(Optional.of("en"));
        when(langProvider.get()).thenReturn(lang);
        
        final NinjaRockerFactory ninjaRockerFactory = new NinjaRockerFactoryImpl();
        
        this.templateEngine = new TemplateEngineRocker(router, messages, ninjaProperties,
                                                        langProvider, templateEngineHelper,
                                                        ninjaRockerFactory);

        output = new ByteArrayOutputStream();
        ResponseStreams responseStreams = mock(ResponseStreams.class);
        when(context.finalizeHeaders(any(Result.class))).thenReturn(responseStreams);
        when(responseStreams.getOutputStream()).thenReturn(output);
    }
    
    public String invoke(Context context, Result result) throws UnsupportedEncodingException {
        templateEngine.invoke(context, result);
        
        return output.toString("UTF-8");
    }
    
    @Test
    public void commonErrorTemplate() throws Exception {
        RockerModel model = com.fizzed.ninja.rocker.views.common_error.template(new Message("Hello!"));
        
        when(context.getContextPath()).thenReturn("/context");
        when(result.getRenderable()).thenReturn(model);
        
        String out = invoke(context, result);
        
        assertThat(out, containsString("href=\"/context/\""));
    }
    
    @Test
    public void autoTemplateNaming() throws Exception {
        when(context.getContextPath()).thenReturn("/context");
        when(templateEngineHelper.getTemplateForResult(context.getRoute(), result, ".rocker.html")).thenReturn("views/context_path.rocker.html");
        
        String out = invoke(context, result);
        
        assertThat(out, is("/context"));
    }
    
    @Test
    public void customApplicationTemplate() throws Exception {
        when(context.getContextPath()).thenReturn("/context");
        when(context.getAttribute("USER", User.class)).thenReturn(new User("Joe"));
        when(templateEngineHelper.getTemplateForResult(context.getRoute(), result, ".rocker.html")).thenReturn("views/application_template.rocker.html");
        
        String out = invoke(context, result);
        
        assertThat(out, is("Joe"));
    }
    
}
