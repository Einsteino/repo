package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.template.CourseTable;
import teammates.ui.template.ElementTag;

public class InstructorHomeCourseAjaxPageData extends PageData {

    private static final int MAX_CLOSED_SESSION_STATS = 3;
    
    private CourseTable courseTable;
    private int index;
    
    public InstructorHomeCourseAjaxPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(int tableIndex, CourseSummaryBundle courseSummary, InstructorAttributes instructor, int pendingComments) {
        this.index = tableIndex;
        this.courseTable = createCourseTable(
                courseSummary.course, instructor, courseSummary.feedbackSessions, pendingComments);
    }
    
    public CourseTable getCourseTable() {
        return courseTable;
    }
    
    public int getIndex() {
        return index;
    }
    
    private CourseTable createCourseTable(CourseAttributes course, InstructorAttributes instructor,
            List<FeedbackSessionAttributes> feedbackSessions, int pendingCommentsCount) {
        String courseId = course.id;
        return new CourseTable(course,
                               createCourseTableLinks(instructor, courseId, pendingCommentsCount),
                               createSessionRows(feedbackSessions, instructor, courseId));
    }
    
    private ElementTag createButton(String text, String className, String href, String tooltip) {
        return new ElementTag(text, "class", className, "href", href, "title", tooltip);
    }
    
    private void addAttributeIf(boolean shouldAdd, ElementTag button, String key, String value) {
        if (shouldAdd) {
            button.setAttribute(key, value);
        }
    }
    
    private List<ElementTag> createCourseTableLinks(InstructorAttributes instructor, String courseId,
            int pendingCommentsCount) {
        String disabled = "disabled";
        String className = "btn btn-primary btn-xs btn-tm-actions course-";
        
        ElementTag enroll = createButton("Enroll",
                                         className + "enroll-for-test",
                                         getInstructorCourseEnrollLink(courseId),
                                         Const.Tooltips.COURSE_ENROLL);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT),
                       enroll, disabled, disabled);
        
        ElementTag view = createButton("View",
                                       className + "view-for-test",
                                       getInstructorCourseDetailsLink(courseId),
                                       Const.Tooltips.COURSE_DETAILS);
        
        ElementTag edit = createButton("Edit",
                                       className + "edit-for-test",
                                       getInstructorCourseEditLink(courseId),
                                       Const.Tooltips.COURSE_EDIT);
        
        ElementTag add = createButton("Add Session",
                                      className + "add-eval-for-test",
                                      getInstructorFeedbacksLink(courseId),
                                      Const.Tooltips.COURSE_ADD_FEEDBACKSESSION);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION),
                       add, disabled, disabled);
        
        ElementTag archive = createButton("Archive",
                                          className + "archive-for-test",
                                          getInstructorCourseArchiveLink(courseId, true, true),
                                          Const.Tooltips.COURSE_ARCHIVE);
        addAttributeIf(true, archive, "onclick", "return toggleArchiveCourseConfirmation('" + courseId + "')");
        
        ElementTag delete = createButton("Delete",
                                         className + "delete-for-test",
                                         getInstructorCourseDeleteLink(courseId, true),
                                         Const.Tooltips.COURSE_DELETE);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE),
                       delete, disabled, disabled);
        addAttributeIf(true, delete, "onclick", "return toggleDeleteCourseConfirmation('" + courseId + "')");
        
        if (pendingCommentsCount <= 0) {
            return Arrays.asList(enroll, view, edit,add, archive, delete);
        } else {
            String pendingGraphic = "<span class=\"badge\">" + pendingCommentsCount + "</span>"
                                    + "<span class=\"glyphicon glyphicon-comment\"></span>"
                                    + "<span class=\"glyphicon glyphicon-arrow-right\"></span>"
                                    + "<span class=\"glyphicon glyphicon-envelope\"></span>";
            ElementTag pending = createButton(
                    pendingGraphic,
                    className + "notify-pending-comments-for-test",
                    getInstructorStudentCommentClearPendingLink(courseId),
                    "Send email notification to recipients of " + pendingCommentsCount
                        + " pending " + (pendingCommentsCount > 1 ? "comments" : "comment"));
    
            return Arrays.asList(enroll, view, edit,add, archive, pending, delete);
        }
    }
    
    private List<Map<String, Object>> createSessionRows(List<FeedbackSessionAttributes> sessions,
            InstructorAttributes instructor, String courseId) {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        int displayedStatsCount = 0;

        Map<String, List<String>> courseIdSectionNamesMap = new HashMap<String, List<String>>();
        try {
            courseIdSectionNamesMap = getCourseIdSectionNamesMap(sessions);
        } catch (EntityDoesNotExistException wonthappen) {
            /*
             * EDNEE is thrown if the courseId of any of the sessions is not valid.
             * However, the sessions passed to this method come from course objects which are
             * retrieved through database query, thus impossible for the courseId to be invalid.
             */
            Assumption.fail("Course that should exist is found to be non-existent");
        }
        
        for (FeedbackSessionAttributes session : sessions) {
            Map<String, Object> columns = new HashMap<String, Object>();
            
            columns.put("name", sanitizeForHtml(session.feedbackSessionName));
            columns.put("tooltip", getInstructorHoverMessageForFeedbackSession(session));
            columns.put("status", getInstructorStatusForFeedbackSession(session));
            columns.put("href", getInstructorFeedbackStatsLink(session.courseId, session.feedbackSessionName));
            
            if (session.isOpened() || session.isWaitingToOpen()) {
                columns.put("recent", " recent");
            } else if (displayedStatsCount < MAX_CLOSED_SESSION_STATS
                       && !TimeHelper.isOlderThanAYear(session.createdTime)) {
                columns.put("recent", " recent");
                ++displayedStatsCount;
            }
            
            columns.put("actions", getInstructorFeedbackSessionActions(session, false, instructor,
                                                                       courseIdSectionNamesMap.get(courseId)));
            
            rows.add(columns);
        }
        
        return rows;
    }
}
