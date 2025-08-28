// src/components/ModalSubmission.js
import React, { useContext, useEffect, useState } from "react";
import "../static/submissionChapter.css";
import "../static/modalSubmission.css"
import Apis, { endpoints } from "../configs/Apis";
import { showError, showSuccess, showWarning } from "../utils/toast";
import { MyUserContext } from "../reducers/MyUserReducer"

const ModalSubmission = ({ open, onClose, chapter, loading, error, responses }) => {
    const [grades, setGrades] = useState({});
    const [feedbacks, setFeedbacks] = useState({});
    const [saving, setSaving] = useState({}); // { [submissionId]: boolean }
    const [saveMsg, setSaveMsg] = useState({}); // { [submissionId]: "ok"/"err" }
    const user = useContext(MyUserContext);

    // Khởi tạo/đồng bộ dữ liệu input khi mở modal hoặc khi responses đổi
    useEffect(() => {
        if (!open) return;
        const nextGrades = {};
        const nextFeedbacks = {};
        (responses || []).forEach((it, idx) => {
            const key = it?.submission?.id ?? idx;
            nextGrades[key] = nextGrades[key] ?? (it?.submission?.grade ?? "");
            nextFeedbacks[key] = nextFeedbacks[key] ?? (it?.submission?.feedback ?? "");
        });
        setGrades(nextGrades);
        setFeedbacks(nextFeedbacks);
    }, [open, responses]);

    const onGradeChange = (key, val) => setGrades((prev) => ({ ...prev, [key]: val }));
    const onFeedbackChange = (key, val) => setFeedbacks((prev) => ({ ...prev, [key]: val }));

    // Lấy thông tin teacher từ context đăng nhập (tên + email)
    const getTeacherMeta = (ctxUser) => {
        const t = (ctxUser && (ctxUser.user || ctxUser)) || {};
        const teacherEmail = t.email || "";
        const teacherName =
            t.name ||
            [t.lastName, t.firstName].filter(Boolean).join(" ").trim() ||
            "Teacher";
        return { teacherEmail, teacherName };
    };

    // Gửi email: TEACHER → STUDENT (đã chấm bài)
    const sendGradedEmail = async ({
        teacherEmail,
        teacherName,
        studentEmail,
        exerciseTitle,
        submissionId,
        grade,
        feedback,
    }) => {
        const payload = {
            studentEmail,   // người NHẬN
            teacherEmail,   // Reply-To
            teacherName,
            exerciseTitle,
            submissionId,
            viewUrl: "",    // để backend tự build link student
            grade: grade ?? null,
            feedback: feedback ?? "",
        };
        try {
            await Apis.post(endpoints.email, payload); // endpoints.email -> "/api/email/send"
            return true;
        } catch (err) {
            console.error("Send graded email error:", err);
            return false;
        }
    };

    // Khi teacher bấm LƯU
    const handleSave = async (submission, question, student, key) => {
        const submissionId = submission?.id;
        const exerciseId = question?.excerciseId; // theo DTO hiện tại
        const studentId = student?.userId ?? student?.id;

        if (!submissionId || !exerciseId || !studentId) {
            setSaveMsg((m) => ({ ...m, [submissionId || key]: "Thiếu submissionId/exerciseId/studentId" }));
            return;
        }

        const url = `${endpoints.submissions}/${submissionId}?exerciseId=${exerciseId}&studentId=${studentId}`;
        const gradeValRaw = grades[key];
        const feedbackVal = feedbacks[key];

        const body = {
            grade: gradeValRaw === "" ? null : Number(gradeValRaw),
            feedback: feedbackVal ?? "",
        };

        try {
            setSaving((s) => ({ ...s, [submissionId]: true }));
            setSaveMsg((m) => ({ ...m, [submissionId]: "" }));

            // 1) Lưu điểm/feedback
            await Apis.put(url, body);

            // 2) Lấy dữ liệu để gửi mail
            const usr = (student && student.user) || {};
            const studentEmail = usr?.email || "";
            const studentName =
                usr?.name || [usr?.lastName, usr?.firstName].filter(Boolean).join(" ").trim() || `#${studentId}`;

            const exerciseTitle =
                (question && question.exerciseTitle) || (chapter && chapter.title) || "Bài tập";

            const { teacherEmail, teacherName } = getTeacherMeta(user);

            // 3) Gửi mail "đã chấm bài" cho student
            const mailed = await sendGradedEmail({
                teacherEmail,
                teacherName,
                studentEmail,
                exerciseTitle,
                submissionId,
                grade: body.grade,
                feedback: body.feedback,
            });

            // 3) Thông báo theo kết quả gửi mail
            if (mailed) {
                showSuccess("Lưu & gửi email thành công");
                setSaveMsg((m) => ({
                    ...m,
                    [submissionId]: "Đã lưu"
                }));
            } else {
                showWarning("Lưu thành công (gửi email lỗi)");
                setSaveMsg((m) => ({
                    ...m,
                    [submissionId]: "Đã lưu (mail lỗi)"
                }));
            }
        } catch (e) {
            console.error("Save grade/feedback error:", e);
            showError("Lưu điểm/feedback thất bại");
            setSaveMsg((m) => ({ ...m, [submissionId]: "Lưu thất bại" }));
        } finally {
            setSaving((s) => ({ ...s, [submissionId]: false }));
        }
    };
    if (!open) return null;
    return (
        <div className="sc-modal-backdrop" onClick={onClose}>
            <div className="sc-modal" onClick={(e) => e.stopPropagation()}>
                {/* Header */}
                <div className="sc-modal-header">
                    <div>{chapter ? `Bài tự luận - ${chapter.title}` : "Bài tự luận"}</div>
                    <button
                        type="button"
                        onClick={onClose}
                        className="btn btn-light"
                        style={{ border: "1px solid #c3c5c9ff" }}
                    >
                        Đóng
                    </button>
                </div>

                {/* Body */}
                <div className="sc-modal-body">
                    {loading && <div className="loading-message">Đang tải bài làm…</div>}
                    {!loading && error && <div className="error-message">{error}</div>}

                    {!loading && !error && (
                        <>
                            {!responses || responses.length === 0 ? (
                                <div className="no-submissions">Chưa có bài tự luận nào.</div>
                            ) : (
                                <ul className="submission-list">
                                    {Object.entries(
                                        responses
                                            .filter((it) => it.submission?.status === "COMPLETED")
                                            .reduce((acc, it) => {
                                                const qid = it.question?.id;
                                                if (!qid) return acc;
                                                if (!acc[qid]) acc[qid] = [];
                                                acc[qid].push(it);
                                                return acc;
                                            }, {})
                                    ).map(([questionId, groupedResponses]) => {
                                        const q = groupedResponses[0]?.question || {};

                                        return (
                                            <li key={questionId} className="submission-item">
                                                {/* Câu hỏi Header */}
                                                <div className="question-header">
                                                    Câu {q.orderIndex ?? "-"}: {q.question || "(Không có nội dung câu hỏi)"}
                                                </div>

                                                {groupedResponses.map((it, idx) => {
                                                    const sub = it.submission || {};
                                                    const stu = sub.student || {};
                                                    const usr = stu.user || {};
                                                    const key = sub.id ?? `${questionId}-${idx}`;
                                                    const isSaving = !!saving[sub.id];

                                                    return (
                                                        <div key={`${key}`} className="student-response-block">
                                                            {/* Thông tin học sinh Grid */}
                                                            <div className="student-info-grid">
                                                                <div className="student-info-item">
                                                                    <span className="student-info-label">Học sinh</span>
                                                                    <span className="student-info-value">
                                                                        {usr?.name || `#${stu.userId ?? ""}`}
                                                                    </span>
                                                                </div>
                                                                <div className="student-info-item">
                                                                    <span className="student-info-label">Email</span>
                                                                    <span className="student-info-value">
                                                                        {usr?.email || "Chưa có email"}
                                                                    </span>
                                                                </div>
                                                                <div className="student-info-item">
                                                                    <span className="student-info-label">Trạng thái</span>
                                                                    <span className={`status-badge ${sub?.status === 'COMPLETED' ? 'status-completed' : 'status-pending'}`}>
                                                                        {sub?.status || "Chưa xác định"}
                                                                    </span>
                                                                </div>
                                                            </div>

                                                            {/* Bài làm */}
                                                            <div className="answer-section">
                                                                <span className="answer-label">Trả lời:</span>
                                                                <div className="answer-content">
                                                                    {it.answerEssay || "(Chưa có câu trả lời)"}
                                                                </div>
                                                            </div>

                                                            {/* Grade và Feedback Grid */}
                                                            <div className="grade-feedback-grid">
                                                                <div className="grade-section">
                                                                    <label className="input-label">Điểm</label>
                                                                    <input
                                                                        type="number"
                                                                        step="0.25"
                                                                        min="0"
                                                                        max="10"
                                                                        value={grades[key] ?? ""}
                                                                        onChange={(e) => onGradeChange(key, e.target.value)}
                                                                        className="grade-input"
                                                                        disabled={isSaving}
                                                                        placeholder="Nhập điểm (0-10)"
                                                                    />
                                                                </div>
                                                                <div className="feedback-section">
                                                                    <label className="input-label">Feedback</label>
                                                                    <textarea
                                                                        rows={3}
                                                                        value={feedbacks[key] ?? ""}
                                                                        onChange={(e) => onFeedbackChange(key, e.target.value)}
                                                                        className="feedback-textarea"
                                                                        placeholder="Nhận xét cho bài làm này…"
                                                                        disabled={isSaving}
                                                                    />
                                                                </div>
                                                            </div>

                                                            {/* Save Action */}
                                                            <div className="save-action">
                                                                <button
                                                                    type="button"
                                                                    className="save-button"
                                                                    onClick={() => handleSave(sub, q, stu, key)}
                                                                    disabled={isSaving}
                                                                >
                                                                    {isSaving ? "Đang lưu..." : "Lưu"}
                                                                </button>
                                                                {saveMsg[sub.id] && (
                                                                    <span className={`save-message ${saveMsg[sub.id] === "Đã lưu" ? 'save-success' : 'save-error'}`}>
                                                                        {saveMsg[sub.id]}
                                                                    </span>
                                                                )}
                                                            </div>
                                                            <hr className="submission-divider" />
                                                        </div>
                                                    );
                                                })}
                                            </li>
                                        );
                                    })}
                                </ul>
                            )}
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ModalSubmission;