package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;

public class FeedbackResponseComment {
    private Long commentId;
    private String extraClass = "";
    private String giverDisplay;
    private String createdAt;
    private String editedAt;
    private String commentText;
    private String feedbackResponseId;
    private String courseId;
    private String questionId;
    private String feedbackSessionName;
    private String responseGiverName;
    private String responseRecipientName;
    private String showCommentToString;
    private String showGiverNameToString;
    private String linkToCommentsPage;
    private List<FeedbackParticipantType> showCommentTo;
    private List<FeedbackParticipantType> showGiverNameTo;
    private boolean withVisibilityIcon;
    private boolean withNotificationIcon;
    private boolean withLinkToCommentsPage;
    private boolean editDeleteEnabled;
    private boolean editDeleteEnabledOnlyOnHover;
    private boolean instructorAllowedToDelete;
    private boolean instructorAllowedToEdit;
    private boolean responseVisibleToRecipient;
    private boolean responseVisibleToGiverTeam;
    private boolean responseVisibleToRecipientTeam;
    private boolean responseVisibleToStudents;
    private boolean responseVisibleToInstructors;

    public FeedbackResponseComment(FeedbackResponseCommentAttributes frc, String giverDisplay) {
        this.commentId = frc.getId();
        this.giverDisplay = giverDisplay;
        this.createdAt = frc.createdAt.toString();
        this.editedAt = frc.getEditedAtText(giverDisplay.equals("Anonymous"));
        this.commentText = frc.commentText.getValue();
    }

    // Used in InstructorFeedbackResponseCommentAjaxPageData for instructorFeedbackResponseCommentsAdd.jsp
    public FeedbackResponseComment(FeedbackResponseCommentAttributes frc, String giverDisplay, 
                                   String giverName, String recipientName, String showCommentToString,
                                   String showGiverNameToString, boolean isResponseVisibleToRecipient,
                                   boolean isResponseVisibleToGiverTeam, boolean isResponseVisibleToRecipientTeam,
                                   boolean isResponseVisibleToStudents, boolean isResponseVisibleToInstructors,
                                   boolean isEditDeleteEnabled, boolean isInstructorAllowedToDelete,
                                   boolean isInstructorAllowedToEdit) {
        this.commentId = frc.getId();
        this.giverDisplay = giverDisplay;
        this.createdAt = frc.createdAt.toString();
        this.editedAt = frc.getEditedAtText(giverDisplay.equals("Anonymous"));
        this.commentText = frc.commentText.getValue();
        this.feedbackResponseId = frc.feedbackResponseId;
        this.courseId = frc.courseId;
        this.feedbackSessionName = frc.feedbackSessionName;
        this.showCommentTo = frc.showCommentTo;
        this.showGiverNameTo = frc.showGiverNameTo;
        this.responseGiverName = giverName;
        this.responseRecipientName = recipientName;
        this.showCommentToString = showCommentToString;
        this.showGiverNameToString = showGiverNameToString;
        this.responseVisibleToRecipient = isResponseVisibleToRecipient;
        this.responseVisibleToGiverTeam = isResponseVisibleToGiverTeam;
        this.responseVisibleToRecipientTeam = isResponseVisibleToRecipientTeam;
        this.responseVisibleToStudents = isResponseVisibleToStudents;
        this.responseVisibleToInstructors = isResponseVisibleToInstructors;
        this.editDeleteEnabled = isEditDeleteEnabled;
        this.instructorAllowedToDelete = isInstructorAllowedToDelete;
        this.instructorAllowedToEdit = isInstructorAllowedToEdit;
    }
    
    // for adding comments
    public FeedbackResponseComment(FeedbackResponseCommentAttributes frc,
                                   String giverName, String recipientName,String showCommentToString,
                                   String showGiverNameToString, boolean isResponseVisibleToRecipient,
                                   boolean isResponseVisibleToGiverTeam, boolean isResponseVisibleToRecipientTeam,
                                   boolean isResponseVisibleToStudents, boolean isResponseVisibleToInstructors,
                                   List<FeedbackParticipantType> showCommentTo, List<FeedbackParticipantType> showGiverNameTo,
                                   boolean isAddEnabled) {
        this.questionId = frc.feedbackQuestionId;
        this.feedbackResponseId = frc.feedbackResponseId;
        this.courseId = frc.courseId;
        this.feedbackSessionName = frc.feedbackSessionName;
        this.responseGiverName = giverName;
        this.responseRecipientName = recipientName;
        this.showCommentToString = showCommentToString;
        this.showGiverNameToString = showGiverNameToString;
        this.responseVisibleToRecipient = isResponseVisibleToRecipient;
        this.responseVisibleToGiverTeam = isResponseVisibleToGiverTeam;
        this.responseVisibleToRecipientTeam = isResponseVisibleToRecipientTeam;
        this.responseVisibleToStudents = isResponseVisibleToStudents;
        this.responseVisibleToInstructors = isResponseVisibleToInstructors;
        this.showCommentTo = showCommentTo;
        this.showGiverNameTo = showGiverNameTo;
    }

    public String getExtraClass() {
        return extraClass;
    }

    public void setExtraClass(String extraClass) {
        this.extraClass = extraClass;
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getGiverDisplay() {
        return giverDisplay;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getEditedAt() {
        return editedAt;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getLinkToCommentsPage() {
        return linkToCommentsPage;
    }

    public String getFeedbackResponseId() {
        return feedbackResponseId;   
    }

    public String getCourseId() {
        return courseId;
    }
    
    public String getQuestionId() {
        return questionId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getResponseGiverName() {
        return responseGiverName;
    }

    public String getResponseRecipientName() {
        return responseRecipientName;
    }

    public String getShowCommentToString() {
        return showCommentToString;
    }

    public String getShowGiverNameToString() {
        return showGiverNameToString;
    }

    public boolean isWithVisibilityIcon() {
        return withVisibilityIcon;
    }

    public boolean isWithNotificationIcon() {
        return withNotificationIcon;
    }

    public boolean isWithLinkToCommentsPage() {
        return withLinkToCommentsPage;
    }

    public boolean isEditDeleteEnabled() {
        return editDeleteEnabled;
    }

    public boolean isEditDeleteEnabledOnlyOnHover() {
        return editDeleteEnabledOnlyOnHover;
    }
    
    public boolean isInstructorAllowedToDelete() {
        return instructorAllowedToDelete;
    }

    public boolean isInstructorAllowedToEdit() {
        return instructorAllowedToEdit;
    }

    public boolean isResponseVisibleToRecipient() {
        return responseVisibleToRecipient;
    }

    public boolean isResponseVisibleToGiverTeam() {
        return responseVisibleToGiverTeam;
    }

    public boolean isResponseVisibleToRecipientTeam() {
        return responseVisibleToRecipientTeam;
    }

    public boolean isResponseVisibleToStudents() {
        return responseVisibleToStudents;
    }

    public boolean isResponseVisibleToInstructors() {
        return responseVisibleToInstructors;
    }

    public boolean isShowCommentToResponseGiver() {
        return showCommentTo.contains(FeedbackParticipantType.GIVER);
    }

    public boolean isShowGiverNameToResponseGiver() {
        return showGiverNameTo.contains(FeedbackParticipantType.GIVER);
    }

    public boolean isShowCommentToResponseRecipient() {
        return showCommentTo.contains(FeedbackParticipantType.RECEIVER);
    }

    public boolean isShowGiverNameToResponseRecipient() {
        return showGiverNameTo.contains(FeedbackParticipantType.RECEIVER);
    }

    public boolean isShowCommentToResponseGiverTeam() {
        return showCommentTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS);
    }

    public boolean isShowGiverNameToResponseGiverTeam() {
        return showGiverNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS);
    }

    public boolean isShowCommentToResponseRecipientTeam() {
        return showCommentTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }

    public boolean isShowGiverNameToResponseRecipientTeam() {
        return showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }

    public boolean isShowCommentToStudents() {
        return showCommentTo.contains(FeedbackParticipantType.STUDENTS);
    }

    public boolean isShowGiverNameToStudents() {
        return showGiverNameTo.contains(FeedbackParticipantType.STUDENTS);
    }

    public boolean isShowCommentToInstructors() {
        return showCommentTo.contains(FeedbackParticipantType.INSTRUCTORS);
    }

    public boolean isShowGiverNameToInstructors() {
        return showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
    }

    public FeedbackResponseComment setLinkToCommentsPage(String linkToCommentsPage) {
        this.withLinkToCommentsPage = true;
        this.linkToCommentsPage = linkToCommentsPage;
        return this;
    }
}
