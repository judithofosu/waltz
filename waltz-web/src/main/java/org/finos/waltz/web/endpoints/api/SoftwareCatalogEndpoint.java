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

import org.finos.waltz.model.change_set.ChangeSet;
import org.finos.waltz.model.software_catalog.SoftwarePackage;
import org.finos.waltz.service.software_catalog.SoftwareCatalogService;
import org.finos.waltz.web.DatumRoute;
import org.finos.waltz.web.ListRoute;
import org.finos.waltz.web.endpoints.Endpoint;
import org.finos.waltz.model.IdSelectionOptions;
import org.finos.waltz.model.software_catalog.SoftwareCatalog;
import org.finos.waltz.model.software_catalog.SoftwareSummaryStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spark.Request;
import spark.Response;

import java.io.IOException;

import static org.finos.waltz.web.WebUtilities.*;
import static org.finos.waltz.web.endpoints.EndpointUtilities.getForDatum;
import static org.finos.waltz.web.endpoints.EndpointUtilities.postForDatum;
import static org.finos.waltz.web.endpoints.EndpointUtilities.getForList;

@Service
public class SoftwareCatalogEndpoint implements Endpoint {

    private static final String BASE_URL = mkPath("api", "software-catalog");

    private final SoftwareCatalogService service;

    @Autowired
    public SoftwareCatalogEndpoint(SoftwareCatalogService service) {
        this.service = service;
    }


    @Override
    public void register() {

        String makeCatalogForAppIdsPath = mkPath(BASE_URL, "apps");
        String calculateStatsForAppIdSelectorPath = mkPath(BASE_URL, "stats");
        String findAllPath = mkPath(BASE_URL, "all");

        DatumRoute<SoftwareCatalog> makeCatalogForAppIdsRoute = (request, response) ->
                service.makeCatalogForAppIds(readIdsFromBody(request));

        DatumRoute<SoftwareSummaryStatistics> calculateStatsForAppIdSelectorRoute = (request, response)
                -> service.calculateStatisticsForAppIdSelector(readIdSelectionOptionsFromBody(request));

        ListRoute<SoftwarePackage> findAllRoute = (request, response) ->
                service.findAll();

        getForDatum(mkPath(BASE_URL, "package-id", ":id"), this::getByPackageIdRoute);
        getForDatum(mkPath(BASE_URL, "version-id", ":id"), this::getByVersionIdRoute);
        getForDatum(mkPath(BASE_URL, "licence-id", ":id"), this::getByLicenceIdRoute);
        postForDatum(mkPath(BASE_URL, "selector"), this::getBySelectorRoute);
        postForDatum(makeCatalogForAppIdsPath, makeCatalogForAppIdsRoute);
        postForDatum(calculateStatsForAppIdSelectorPath, calculateStatsForAppIdSelectorRoute);
        getForList(findAllPath, findAllRoute);

    }


    private SoftwareCatalog getByPackageIdRoute(Request request, Response response) {
        long id = getId(request);
        return service.getByPackageId(id);
    }


    private SoftwareCatalog getByVersionIdRoute(Request request, Response response) {
        long id = getId(request);
        return service.getByVersionId(id);
    }


    private SoftwareCatalog getByLicenceIdRoute(Request request, Response response) {
        long id = getId(request);
        return service.getByLicenceId(id);
    }


    private SoftwareCatalog getBySelectorRoute(Request request, Response response) throws IOException {
        IdSelectionOptions options = readIdSelectionOptionsFromBody(request);
        return service.getBySelector(options);
    }

}
