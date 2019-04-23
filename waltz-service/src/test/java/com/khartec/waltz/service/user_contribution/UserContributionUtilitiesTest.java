package com.khartec.waltz.service.user_contribution;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.khartec.waltz.common.ListUtilities.asList;
import static com.khartec.waltz.service.user_contribution.UserContributionUtilities.findWindow;

public class UserContributionUtilitiesTest {

    @Test
    public void indexOnStartOfBoundaryReturnCorrectWindow(){
        List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        List<Integer> startBoundaryWindow = findWindow(list, 1, 10);

        Assert.assertArrayEquals(
                "Start boundary window is incorrect",
                new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                startBoundaryWindow.toArray());
    }


    @Test
    public void indexInMiddleReturnCorrectWindow(){
        List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        List<Integer> endBoundaryWindow = findWindow(list, 8, 10);

        Assert.assertArrayEquals(
                "Middle boundary window is incorrect",
                new Integer[]{4, 5, 6, 7, 8, 9, 10, 11, 12, 13},
                endBoundaryWindow.toArray());
    }


    @Test
    public void indexOnEndOfBoundaryReturnCorrectWindow(){
        List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        List<Integer> endBoundaryWindow = findWindow(list, 15, 10);

        Assert.assertArrayEquals(
                "End boundary window is incorrect",
                new Integer[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                endBoundaryWindow.toArray());
    }


    @Test
    public void indexNearStartOfBoundaryReturnCorrectWindow(){
        List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        List<Integer> endBoundaryWindow = findWindow(list, 3, 10);

        Assert.assertArrayEquals(
                "Near start boundary window is incorrect",
                new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                endBoundaryWindow.toArray());
    }


    @Test
    public void indexNearEndOfBoundaryReturnCorrectWindow(){
        List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        List<Integer> endBoundaryWindow = findWindow(list, 12, 10);

        Assert.assertArrayEquals(
                "Near end boundary window is incorrect",
                new Integer[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                endBoundaryWindow.toArray());
    }


    @Test
    public void oddValueOfWindowSizeReturnsCorrectWindow(){
        List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        List<Integer> endBoundaryWindow = findWindow(list, 8, 5);

        Assert.assertArrayEquals(
                "Odd window size does not return correctly",
                new Integer[]{6, 7, 8, 9, 10},
                endBoundaryWindow.toArray());
    }


    @Test
    public void ifWindowSizeGreaterThanListLengthFullListReturned(){
        List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        List<Integer> endBoundaryWindow = findWindow(list, 8, 20);

        Assert.assertArrayEquals(
                "window size is greater than the length of the list",
                new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                endBoundaryWindow.toArray());
    }


}
