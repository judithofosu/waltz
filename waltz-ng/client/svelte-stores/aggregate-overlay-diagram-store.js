import {remote} from "./remote";
import {checkIsEntityRef} from "../common/checks";


export function mkOverlayDiagramStore() {

    const getById = (id, force = false) => {
        return remote
            .fetchViewDatum(
                "GET",
                `api/aggregate-overlay-diagram/id/${id}`,
                {force});
    };


    const findAll = (force = false) => {
        return remote
            .fetchViewList(
                "GET",
                "api/aggregate-overlay-diagram/all",
                [],
                {force});
    };


    const findAppCountsForDiagram = (diagramId, vantagePointRef, futureDate, force = false) => {
        return remote
            .fetchViewList(
                "POST",
                `api/aggregate-overlay-diagram/diagram-id/${diagramId}/app-count-widget/${futureDate}`,
                vantagePointRef,
                {force});
    };

    const findTargetAppCostForDiagram = (diagramId, vantagePointRef, futureDate, force = false) => {
        return remote
            .fetchViewList(
                "POST",
                `api/aggregate-overlay-diagram/diagram-id/${diagramId}/target-app-cost-widget/${futureDate}`,
                vantagePointRef,
                {force});
    };

    const findAppCostForDiagram = (diagramId, appCostParameters, force = false) => {
        return remote
            .fetchViewList(
                "POST",
                `api/aggregate-overlay-diagram/diagram-id/${diagramId}/app-cost-widget`,
                appCostParameters,
                {force});
    };

    const findAppAssessmentsForDiagram = (diagramId, assessmentId, vantagePointRef, force = false) => {
        return remote
            .fetchViewList(
                "POST",
                `api/aggregate-overlay-diagram/diagram-id/${diagramId}/app-assessment-widget/${assessmentId}`,
                vantagePointRef,
                {force});
    };

    const findBackingEntitiesForDiagram = (diagramId, force = false) => {
        return remote
            .fetchViewList(
                "GET",
                `api/aggregate-overlay-diagram/diagram-id/${diagramId}/backing-entity-widget`,
                null,
                {force});
    };

    return {
        getById,
        findAll,
        findAppCountsForDiagram,
        findTargetAppCostForDiagram,
        findAppCostForDiagram,
        findAppAssessmentsForDiagram,
        findBackingEntitiesForDiagram
    };
}


export const aggregateOverlayDiagramStore = mkOverlayDiagramStore();