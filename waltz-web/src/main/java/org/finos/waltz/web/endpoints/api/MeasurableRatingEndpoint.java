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

import org.finos.waltz.service.measurable_rating.MeasurableRatingService;
import org.finos.waltz.service.user.UserRoleService;
import org.finos.waltz.web.ListRoute;
import org.finos.waltz.web.endpoints.Endpoint;
import org.finos.waltz.model.EntityKind;
import org.finos.waltz.model.EntityReference;
import org.finos.waltz.model.UserTimestamp;
import org.finos.waltz.model.measurable_rating.*;
import org.finos.waltz.model.tally.MeasurableRatingTally;
import org.finos.waltz.model.tally.Tally;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.finos.waltz.web.WebUtilities.*;
import static org.finos.waltz.web.endpoints.EndpointUtilities.*;
import static org.finos.waltz.common.Checks.checkNotNull;
import static org.finos.waltz.common.StringUtilities.firstChar;
import static org.finos.waltz.model.EntityReference.mkRef;

@Service
public class MeasurableRatingEndpoint implements Endpoint {

    private static final String BASE_URL = mkPath("api", "measurable-rating");


    private final MeasurableRatingService measurableRatingService;
    private final UserRoleService userRoleService;


    @Autowired
    public MeasurableRatingEndpoint(MeasurableRatingService measurableRatingService,
                                    UserRoleService userRoleService) {
        checkNotNull(measurableRatingService, "measurableRatingService cannot be null");
        checkNotNull(userRoleService, "userRoleService cannot be null");

        this.measurableRatingService = measurableRatingService;
        this.userRoleService = userRoleService;
    }


    @Override
    public void register() {
        String findForEntityPath = mkPath(BASE_URL, "entity", ":kind", ":id");
        String modifyMeasurableForEntityPath = mkPath(BASE_URL, "entity", ":kind", ":id", "measurable", ":measurableId");
        String modifyCategoryForEntityPath = mkPath(BASE_URL, "entity", ":kind", ":id", "category", ":categoryId");
        String findByMeasurableSelectorPath = mkPath(BASE_URL, "measurable-selector");
        String findByAppSelectorPath = mkPath(BASE_URL, "app-selector");
        String findByCategoryPath = mkPath(BASE_URL, "category", ":id");
        String countByMeasurableCategoryPath = mkPath(BASE_URL, "count-by", "measurable", "category", ":id");
        String statsByAppSelectorPath = mkPath(BASE_URL, "stats-by", "app-selector");
        String statsForRelatedMeasurablePath = mkPath(BASE_URL, "related-stats", "measurable");

        ListRoute<MeasurableRating> findForEntityRoute = (request, response)
                -> measurableRatingService.findForEntity(getEntityReference(request));

        ListRoute<MeasurableRating> findByMeasurableSelectorRoute = (request, response)
                -> measurableRatingService.findByMeasurableIdSelector(readIdSelectionOptionsFromBody(request));

        ListRoute<MeasurableRating> findByAppSelectorRoute = (request, response)
                -> measurableRatingService.findByAppIdSelector(readIdSelectionOptionsFromBody(request));

        ListRoute<MeasurableRating> findByCategoryRoute = (request, response)
                -> measurableRatingService.findByCategory(getId(request));

        ListRoute<Tally<Long>> countByMeasurableCategoryRoute = (request, response)
                -> measurableRatingService.tallyByMeasurableCategoryId(getId(request));

        ListRoute<MeasurableRatingTally> statsByAppSelectorRoute = (request, response)
                -> measurableRatingService.statsByAppSelector(readIdSelectionOptionsFromBody(request));

        ListRoute<MeasurableRatingTally> statsForRelatedMeasurableRoute = (request, response)
                -> measurableRatingService.statsForRelatedMeasurable(readIdSelectionOptionsFromBody(request));

        getForList(findForEntityPath, findForEntityRoute);
        postForList(findByMeasurableSelectorPath, findByMeasurableSelectorRoute);
        postForList(findByAppSelectorPath, findByAppSelectorRoute);
        getForList(findByCategoryPath, findByCategoryRoute);
        postForList(modifyMeasurableForEntityPath, this::saveRoute);
        deleteForList(modifyMeasurableForEntityPath, this::removeRoute);
        deleteForList(modifyCategoryForEntityPath, this::removeCategoryRoute);
        getForList(countByMeasurableCategoryPath, countByMeasurableCategoryRoute);
        postForList(statsForRelatedMeasurablePath, statsForRelatedMeasurableRoute);
        postForList(statsByAppSelectorPath, statsByAppSelectorRoute);
    }

    private Collection<MeasurableRating> removeCategoryRoute(Request request, Response z) {

        EntityReference ref = getEntityReference(request);
        long categoryId = getLong(request, "categoryId");

        requireRole(userRoleService, request, measurableRatingService.getRequiredRatingEditRole(mkRef(EntityKind.MEASURABLE_CATEGORY, categoryId)));

        return measurableRatingService.removeForCategory(ref, categoryId, getUsername(request));
    }


    private Collection<MeasurableRating> saveRoute(Request request, Response z) throws IOException {
        SaveMeasurableRatingCommand command = mkCommand(request);
        requireRole(userRoleService, request, measurableRatingService.getRequiredRatingEditRole(mkRef(EntityKind.MEASURABLE, command.measurableId())));
        return measurableRatingService.save(command, false);
    }


    private Collection<MeasurableRating> removeRoute(Request request, Response z) throws IOException {
        long measurableId = getLong(request, "measurableId");
        String username = getUsername(request);

        requireRole(userRoleService, request, measurableRatingService.getRequiredRatingEditRole(mkRef(EntityKind.MEASURABLE, measurableId)));

        RemoveMeasurableRatingCommand command = ImmutableRemoveMeasurableRatingCommand.builder()
                .entityReference(getEntityReference(request))
                .measurableId(measurableId)
                .lastUpdate(UserTimestamp.mkForUser(username))
                .build();
        return measurableRatingService.remove(command);
    }


    private SaveMeasurableRatingCommand mkCommand(Request request) throws IOException {
        String username = getUsername(request);

        Map<String, String> body = readBody(request, Map.class);
        String plannedDateString = body.get("plannedDate");
        Optional<LocalDate> plannedDate = plannedDateString != null
                ? Optional.of(LocalDate.parse(plannedDateString))
                : Optional.empty();

        return ImmutableSaveMeasurableRatingCommand.builder()
                .entityReference(getEntityReference(request))
                .measurableId(getLong(request, "measurableId"))
                .rating(firstChar(body.getOrDefault("rating", "Z"), 'Z'))
                .previousRating(firstChar(body.getOrDefault("previousRating", "")))
                .description(body.getOrDefault("description", ""))
                .lastUpdate(UserTimestamp.mkForUser(username))
                .provenance("waltz")
                .build();
    }

}