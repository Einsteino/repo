package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;
import teammates.ui.controller.InstructorFeedbackResultsPageData.ViewType;

public class InstructorFeedbackResultsPageAction extends Action {

    private static final String ALL_SECTION_OPTION = "All";
    private static final int DEFAULT_QUERY_RANGE = 1000;
    private static final int DEFAULT_SECTION_QUERY_RANGE = 2500;
    private static final int QUERY_RANGE_FOR_AJAX_TESTING = 5;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String needAjax = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX);

        int queryRange;
        if (needAjax != null) {
            queryRange = QUERY_RANGE_FOR_AJAX_TESTING;
        } else {
            queryRange = DEFAULT_QUERY_RANGE;
        }

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);

        statusToAdmin = "Show instructor feedback result page<br>"
                      + "Session Name: " + feedbackSessionName + "<br>"
                      + "Course ID: " + courseId;

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;

        new GateKeeper().verifyAccessible(instructor, session, !isCreatorOnly);

        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        String selectedSection = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION);

        String showStats = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS);
        String groupByTeam = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM);
        String sortType = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE);

        if (selectedSection == null) {
            selectedSection = ALL_SECTION_OPTION;
        }
        
        // this is for ajax loading of the html table in the modal 
        // "(Non-English characters not displayed properly in the downloaded file? click here)"
        // TODO move into another action and another page data class 
        boolean isLoadingCsvResultsAsHtml = getRequestParamAsBoolean(Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED);
        if (isLoadingCsvResultsAsHtml) {
            return createAjaxResultForCsvTableLoadedInHtml(courseId, feedbackSessionName, instructor, data, selectedSection);
        } else {
            data.sessionResultsHtmlTableAsString = "";
            data.ajaxStatus = "";
        }

        String startIndex = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_MAIN_INDEX);
        if (startIndex != null) {
            data.startIndex = Integer.parseInt(startIndex);
        }

        if (sortType == null) {
            // default view: sort by question, statistics shown, grouped by team.
            showStats = new String("on");
            groupByTeam = new String("on");
            sortType = new String("question");
        }
        
        data.sections = logic.getSectionNamesForCourse(courseId);
        String questionNumStr = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        if ("All".equals(selectedSection) && questionNumStr == null) {
            // bundle for all questions and all sections  
            data.bundle = logic
                    .getFeedbackSessionResultsForInstructorWithinRangeFromView(feedbackSessionName, courseId,
                                                                               instructor.email,
                                                                               queryRange, sortType);
        } else if (sortType.equals("question")) {
            if (questionNumStr == null) {
                // bundle for all questions, with a selected section
                data.bundle = logic.getFeedbackSessionResultsForInstructorInSection(feedbackSessionName, courseId,
                                                                                    instructor.email,
                                                                                    selectedSection);
            } else {
                // bundle for a specific question, with a selected section
                int questionNum = Integer.parseInt(questionNumStr);
                data.bundle = logic.getFeedbackSessionResultsForInstructorFromQuestion(feedbackSessionName, courseId, 
                                                                                       instructor.email, questionNum);
            }
        } else if (sortType.equals("giver-question-recipient")
                || sortType.equals("giver-recipient-question")) {
            data.bundle = logic
                    .getFeedbackSessionResultsForInstructorFromSectionWithinRange(feedbackSessionName, courseId,
                                                                                  instructor.email,
                                                                                  selectedSection,
                                                                                  DEFAULT_SECTION_QUERY_RANGE);
        } else if (sortType.equals("recipient-question-giver")
                || sortType.equals("recipient-giver-question")) {
            data.bundle = logic
                    .getFeedbackSessionResultsForInstructorToSectionWithinRange(feedbackSessionName, courseId,
                                                                                instructor.email,
                                                                                selectedSection,
                                                                                DEFAULT_SECTION_QUERY_RANGE);
        }

        if (data.bundle == null) {
            throw new EntityDoesNotExistException("Feedback session " + feedbackSessionName
                                                  + " does not exist in " + courseId + ".");
        }

        // Warning for section wise viewing in case of many responses.
        if (selectedSection.equals(ALL_SECTION_OPTION) && !data.bundle.isComplete) {
            // not tested because the test data is not large enough to make this happen
            statusToUser.add(Const.StatusMessages.FEEDBACK_RESULTS_SECTIONVIEWWARNING);
            isError = true;
        }
        

        switch (sortType) {
            case "question":
                data.initForViewByQuestion(instructor, selectedSection, showStats, groupByTeam);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION, data);
            case "recipient-giver-question":
                data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam, 
                                              ViewType.RECIPIENT_GIVER_QUESTION);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
            case "giver-recipient-question":
                data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam,
                                              ViewType.GIVER_RECIPIENT_QUESTION);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_RECIPIENT_QUESTION, data);
            case "recipient-question-giver":
                data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam,
                                              ViewType.RECIPIENT_QUESTION_GIVER);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_QUESTION_GIVER, data);
            case "giver-question-recipient":
                data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam, 
                                              ViewType.GIVER_QUESTION_RECIPIENT);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_QUESTION_RECIPIENT, data);
            default:
                sortType = "recipient-giver-question";
                data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam, 
                                              ViewType.RECIPIENT_GIVER_QUESTION);
                return createShowPageResult(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
        }
    }

    private ActionResult createAjaxResultForCsvTableLoadedInHtml(String courseId, String feedbackSessionName,
                                    InstructorAttributes instructor, InstructorFeedbackResultsPageData data, 
                                    String selectedSection)
                                    throws EntityDoesNotExistException {
        try {
            if (!selectedSection.contentEquals(ALL_SECTION_OPTION)) {
                data.sessionResultsHtmlTableAsString = StringHelper.csvToHtmlTable(
                        logic.getFeedbackSessionResultSummaryInSectionAsCsv(courseId, feedbackSessionName,
                                                                            instructor.email, selectedSection));
            } else {
                data.sessionResultsHtmlTableAsString = StringHelper.csvToHtmlTable(
                        logic.getFeedbackSessionResultSummaryAsCsv(courseId, feedbackSessionName,
                                                                   instructor.email));
            }
        } catch (ExceedingRangeException e) {
            // not tested as the test file is not large enough to reach this catch block
            data.sessionResultsHtmlTableAsString = "";
            data.ajaxStatus = "There are too many responses. Please download the feedback results by section.";
        }

        return createAjaxResult(data);
    }

}
