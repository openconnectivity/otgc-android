/*
 * *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  ******************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  ******************************************************************
 */

package org.openconnectivity.otgc.client.domain.usecase;

import android.os.Build;
import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openconnectivity.otgc.client.domain.model.DynamicUiElement;
import org.openconnectivity.otgc.client.domain.model.DynamicUiProperty;
import org.openconnectivity.otgc.common.domain.model.MethodNotSupportedByApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import io.swagger.parser.SwaggerParser;

public class UiFromSwaggerUseCase {

    @Inject
    UiFromSwaggerUseCase() {

    }

    public Single<List<DynamicUiElement>> execute(JSONObject jsonSwagger) {
        return Single.create(emitter -> {
            // SwaggerParser uses java.nio.file.Path class, that is not officially supported
            // until Android O. A lower version of Android should parse the introspection
            // programmatically.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    List<DynamicUiElement> elements = jsonSwaggerToUi(jsonSwagger);
                    Collections.sort(elements,
                            (e1, e2) -> e1.getPath().compareTo(e2.getPath()));
                    emitter.onSuccess(elements);
                } catch (JSONException e) {
                    emitter.onError(e);
                }
            } else {
                // TODO Parse introspection programmatically
                throw new MethodNotSupportedByApiException();
            }
        });
    }

    private List<DynamicUiElement> jsonSwaggerToUi(@NonNull JSONObject jsonSwagger) throws JSONException {
        List<DynamicUiElement> dynamicUi = new ArrayList<>();

        Swagger swagger = new SwaggerParser().readWithInfo(jsonSwagger.toString(), false)
                .getSwagger();

        for (Map.Entry<String, Path> pathEntry : swagger.getPaths().entrySet()) {
            DynamicUiElement uiElement = new DynamicUiElement();
            uiElement.setPath(pathEntry.getKey());
            String definitionName = getSchemaTitle(pathEntry.getValue());
            if (definitionName != null && !definitionName.isEmpty()) {
                uiElement.setResourceTypes(getResourceTypes(jsonSwagger, definitionName));
                uiElement.setInterfaces(getInterfaces(swagger, definitionName));
                uiElement.setProperties(getProperties(swagger, definitionName));
                uiElement.setSupportedOperations(getSupportedOperations(pathEntry.getValue()));
            }

            dynamicUi.add(uiElement);
        }

        return dynamicUi;
    }

    private String getSchemaTitle(Path path) {
        String schemaTitle = null;
        List<Operation> operations = path.getOperations();
        if (operations != null && !operations.isEmpty()) {
            Map<String, Response> responses = operations.get(0).getResponses();
            String schemaRef = responses.get("200").getResponseSchema().getReference();
            schemaTitle = schemaRef.substring(14);
        }
        return schemaTitle;
    }

    private List<String> getResourceTypes(JSONObject jsonSwagger, String definitionName) throws JSONException {
        List<String> resourceTypes = new ArrayList<>();

        JSONArray jsonRt = jsonSwagger.getJSONObject("definitions")
                .getJSONObject(definitionName)
                .getJSONObject("properties")
                .getJSONObject("rt")
                .getJSONArray("default");

        for (int i = 0; i < jsonRt.length(); i++) {
            resourceTypes.add(jsonRt.getString(i));
        }

        return resourceTypes;
    }

    private List<String> getInterfaces(Swagger swagger, String definitionName) {
        ArrayProperty interfaces = (ArrayProperty) swagger.getDefinitions().get(definitionName).getProperties().get("if");
        StringProperty interfaceItems = (StringProperty) interfaces.getItems();

        return interfaceItems.getEnum();
    }

    private List<DynamicUiProperty> getProperties(Swagger swagger, String definitionName) {
        List<DynamicUiProperty> properties = new ArrayList<>();
        for (Map.Entry<String, Property> property : swagger.getDefinitions().get(definitionName).getProperties().entrySet()) {
            if (!property.getKey().equals("rt")
                    && !property.getKey().equals("if")) {
                DynamicUiProperty uiProperty = new DynamicUiProperty(
                        property.getKey(),
                        property.getValue().getType(),
                        property.getValue().getReadOnly() != null ? property.getValue().getReadOnly() : false);
                properties.add(uiProperty);
            }
        }
        return properties;
    }

    private List<String> getSupportedOperations(Path path) {
        List<String> supportedOperations = new ArrayList<>();
        if (path.getGet() != null) {
            supportedOperations.add("get");
        }
        if (path.getPost() != null) {
            supportedOperations.add("post");
        }
        if (path.getPut() != null) {
            supportedOperations.add("put");
        }
        if (path.getDelete() != null) {
            supportedOperations.add("delete");
        }

        return supportedOperations;
    }
}
