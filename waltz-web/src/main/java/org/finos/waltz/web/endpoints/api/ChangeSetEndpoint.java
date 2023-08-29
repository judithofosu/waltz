/*
 * Waltz - Enterprise Architecture
 * Copyright (C) 2016, 2017, 2018, 2019 Waltz open source project
 * See README.md for more information
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
 * See the License for the specific
 *
 */

package org.finos.waltz.web.endpoints.api;


import org.finos.waltz.service.change_set.ChangeSetService;
import org.finos.waltz.web.DatumRoute;
import org.finos.waltz.web.ListRoute;
import org.finos.waltz.web.endpoints.Endpoint;
import org.finos.waltz.model.EntityReference;
import org.finos.waltz.model.change_set.ChangeSet;
import org.finos.waltz.web.WebUtilities;
import org.finos.waltz.web.endpoints.EndpointUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.Long.parseLong;
import static org.finos.waltz.common.Checks.checkNotNull;


@Service
public class ChangeSetEndpoint implements Endpoint {

    private static final String BASE_URL = WebUtilities.mkPath("api", "change-set");

    private final ChangeSetService changeSetService;


    @Autowired
    public ChangeSetEndpoint(ChangeSetService changeSetService) {
        checkNotNull(changeSetService, "changeSetService cannot be null");
        this.changeSetService = changeSetService;
    }


    @Override
    public void register() {
        String getByIdPath = WebUtilities.mkPath(BASE_URL, "id", ":id");
        String findByParentRefPath = WebUtilities.mkPath(BASE_URL, "parent", ":kind", ":id");
        String findByPersonPath = WebUtilities.mkPath(BASE_URL, "person", ":employeeId");
        String findBySelectorPath = WebUtilities.mkPath(BASE_URL, "selector");
        String findAllPath = WebUtilities.mkPath(BASE_URL, "all");


        DatumRoute<ChangeSet> getByIdRoute = (req, res) -> {
            String id = req.params("id");
            return changeSetService
                    .getById(parseLong(id));
        };

        ListRoute<ChangeSet> findByEntityRefRoute = (request, response) -> {
            EntityReference entityReference = WebUtilities.getEntityReference(request);
            return changeSetService.findByParentRef(entityReference);
        };

        ListRoute<ChangeSet> findBySelectorRoute = (request, response) ->
                changeSetService.findBySelector(WebUtilities.readIdSelectionOptionsFromBody(request));

        ListRoute<ChangeSet> findByPersonRoute = (request, response) -> {
            String employeeId = request.params("employeeId");
            return changeSetService.findByPerson(employeeId);
        };

        ListRoute<ChangeSet> findAllRoute = (request, response) ->
                changeSetService.findAll();

        EndpointUtilities.getForDatum(getByIdPath, getByIdRoute);
        EndpointUtilities.getForList(findByPersonPath, findByPersonRoute);
        EndpointUtilities.getForList(findByParentRefPath, findByEntityRefRoute);
        EndpointUtilities.postForList(findBySelectorPath, findBySelectorRoute);
        EndpointUtilities.getForList(findAllPath, findAllRoute);
    }



}
