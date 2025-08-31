// components/ExerciseSection.js
import React, { useEffect, useState, useCallback, useMemo, useContext } from 'react';
import Apis, { endpoints, authApis } from '../configs/Apis';
import { MyUserContext } from '../reducers/MyUserReducer';
import { SidebarContext } from '../reducers/SidebarContext'; // ⬅️ dùng layout offset
import '../static/exerciseSection.css';
import AnswerSection from './AnswerSection';
import ModalExercise from './ModalExercise';
import { showSuccess, showError } from '../utils/toast';
import ModalQuestion from './ModalQuestion';
import ShowGrade from './ShowGrade';

const qidOf = (q) => (q?.id ?? q?.questionId ?? null);
const exIdOfSubmission = (s) => (s?.exerciseId ?? s?.exercise?.id ?? null);

const ExerciseSection = ({ chapterId, role = 'TEACHER' }) => {
    const [loading, setLoading] = useState(false);
    const [exercises, setExercises] = useState([]);
    const [expanded, setExpanded] = useState(new Set());
    const [qState, setQState] = useState(new Map());
    const [selected, setSelected] = useState({});       // MCQ: { [questionId]: answerId }
    const [essayDrafts, setEssayDrafts] = useState({}); // ESSAY: { [questionId]: content }

    const canManage = role === 'TEACHER';

    const [openExModal, setOpenExModal] = useState(false);
    const [editingEx, setEditingEx] = useState(null);
    const [openQModal, setOpenQModal] = useState(false);
    const [editingQ, setEditingQ] = useState(null);
    const [currentExId, setCurrentExId] = useState(null);

    // Current user
    const user = useContext(MyUserContext);
    const isStudent = (user?.role || '').toUpperCase() === 'STUDENT';
    const studentId = user?.id;

    // Layout offsets để không đè header/sidebar
    const { collapsed } = useContext(SidebarContext) || {};
    const leftOffset = collapsed ? 60 : 220; // khớp sidebar.css
    const topOffset = 80;                    // khớp header.css

    // Submission cache theo exercise
    const [submissionByExercise, setSubmissionByExercise] = useState({});
    const [saving, setSaving] = useState({});
    const [submitting, setSubmitting] = useState({});
    // Modal để hiển thị điểm 
    const [gradeModal, setGradeModal] = useState({ open: false, exerciseId: null, exerciseTitle: '', submissionId: null, initialGrade: null });

    // ===== Helpers =====
    const reloadExercises = useCallback(async () => {
        setLoading(true);
        try {
            const res = await Apis.get(`${endpoints.excercises}/chapter/${chapterId}`);
            setExercises(res.data?.items || []);
        } catch (e) {
            showError('Không tải được danh sách bài tập');
        } finally {
            setLoading(false);
        }
    }, [chapterId]);

    useEffect(() => {
        if (chapterId) reloadExercises();
    }, [chapterId, reloadExercises]);

    const fetchQuestions = useCallback(async (exerciseId) => {
        setQState(prev => new Map(prev).set(exerciseId, { loading: true, items: [], error: null }));
        try {
            const url = endpoints.questionsByExercise(exerciseId);
            const res = await Apis.get(url, { params: { sort: 'orderIndex', dir: 'ASC', page: 0, size: 100 } });
            const items = res.data?.items || [];
            setQState(prev => new Map(prev).set(exerciseId, { loading: false, items, error: null }));
        } catch (e) {
            setQState(prev => new Map(prev).set(exerciseId, { loading: false, items: [], error: 'Load câu hỏi thất bại' }));
        }
    }, []);

    const toggle = (exerciseId) => {
        const next = new Set(expanded);
        if (next.has(exerciseId)) {
            next.delete(exerciseId);
            setExpanded(next);
        } else {
            next.add(exerciseId);
            setExpanded(next);
            if (!qState.get(exerciseId)) fetchQuestions(exerciseId);
            // Khi Student mở bài => nạp lại submission + responses đã lưu
            if (isStudent && studentId) {
                loadSubmissionState(exerciseId);
            }
        }
    };

    const openCreateExercise = () => { if (canManage) { setEditingEx(null); setOpenExModal(true); } };
    const openEditExercise = (ex, e) => { e.stopPropagation(); if (canManage) { setEditingEx(ex); setOpenExModal(true); } };
    const deleteExercise = async (id, e) => {
        e.stopPropagation();
        if (!canManage) return;
        if (!window.confirm('Bạn có chắc muốn xóa bài tập này?')) return;
        try {
            await Apis.delete(`${endpoints.excercises}/${id}`);
            await reloadExercises();
            showSuccess('Xóa bài tập thành công.');
        } catch (err) {
            console.error(err);
            showError('Xóa bài tập thất bại. Vui lòng thử lại.');
        }
    };

    const openCreateQuestion = (exerciseId) => {
        if (!canManage) return;
        setCurrentExId(exerciseId);
        setEditingQ(null);
        setOpenQModal(true);
    };

    const openEditQuestion = (exerciseId, q, e) => {
        e.stopPropagation();
        if (!canManage) return;
        setCurrentExId(exerciseId);
        setEditingQ(q);
        setOpenQModal(true);
    };

    const deleteQuestion = async (exerciseId, questionId, e) => {
        e.stopPropagation();
        if (!canManage) return;
        if (!window.confirm('Bạn có chắc muốn xóa câu hỏi này?')) return;
        try {
            await Apis.delete(`${endpoints.questions}/${questionId}`);
            await fetchQuestions(exerciseId);
            showSuccess('Xóa câu hỏi thành công.');
        } catch (err) {
            console.error(err);
            showError('Xóa câu hỏi thất bại. Vui lòng thử lại.');
        }
    };

    // ---- Submissions & Responses ----

    // Tìm submission hiện có để hiển thị lại dữ liệu
    async function getExistingSubmission(exerciseId) {
        // 1) ưu tiên DRAFT
        try {
            const { data } = await Apis.get(
                `${endpoints.submissions}/exercise/${exerciseId}`,
                { params: { studentId, status: 'DRAFT' } }
            );
            const arr = Array.isArray(data) ? data : (data?.items || []);
            const sub = arr.find(x => exIdOfSubmission(x) === exerciseId);
            if (sub) return sub;
        } catch (_) { }

        // 2) nếu không có DRAFT, ưu tiên GRADED, rồi COMPLETED (lấy bản mới nhất theo submittedAt)
        const pickLatest = (xs = []) => {
            const onlyThisExercise = xs.filter(x => exIdOfSubmission(x) === exerciseId);
            if (!onlyThisExercise.length) return null;
            onlyThisExercise.sort((a, b) => new Date(b.submittedAt || 0) - new Date(a.submittedAt || 0));
            return onlyThisExercise[0];
        };

        try {
            const { data } = await Apis.get(
                `${endpoints.submissions}/exercise/${exerciseId}`,
                { params: { studentId, status: 'GRADED' } }
            );
            const graded = Array.isArray(data) ? data : (data?.items || []);
            const latest = pickLatest(graded);
            if (latest) return latest;
        } catch (_) { }

        try {
            const { data } = await Apis.get(
                `${endpoints.submissions}/exercise/${exerciseId}`,
                { params: { studentId, status: 'COMPLETED' } }
            );
            const completed = Array.isArray(data) ? data : (data?.items || []);
            const latest = pickLatest(completed);
            if (latest) return latest;
        } catch (_) { }

        return null;
    }

    // Nạp lại selected/essayDrafts từ BE theo submissionId
    async function hydrateResponsesFromServer(submissionId) {
        try {
            const [mcqRes, essayRes] = await Promise.all([
                Apis.get(`${endpoints['mcq-responses']}/submission/${submissionId}`),
                Apis.get(`${endpoints['essay-responses']}/submission/${submissionId}`)
            ]);

            const mcqArr = Array.isArray(mcqRes.data) ? mcqRes.data : (mcqRes.data?.items || []);
            const essayArr = Array.isArray(essayRes.data) ? essayRes.data : (essayRes.data?.items || []);

            const selUpdate = {};
            mcqArr.forEach(it => {
                const qid = it.questionId ?? it.question?.id ?? it.exerciseQuestionId ?? it.id;
                const ansId = it.answerId ?? it.answer?.id ?? it.selectedAnswerId;
                if (qid != null && ansId != null) selUpdate[qid] = ansId;
            });

            const essayUpdate = {};
            essayArr.forEach(it => {
                const qid = it.questionId ?? it.question?.id ?? it.exerciseQuestionId ?? it.id;
                const content = it.answerEssay ?? it.content ?? it.text ?? '';
                if (qid != null) essayUpdate[qid] = content;
            });

            if (Object.keys(selUpdate).length) setSelected(prev => ({ ...prev, ...selUpdate }));
            if (Object.keys(essayUpdate).length) setEssayDrafts(prev => ({ ...prev, ...essayUpdate }));
        } catch (e) {
            console.warn('hydrateResponsesFromServer failed', e);
        }
    }

    // Đảm bảo có DRAFT khi lưu; khi chỉ hiển thị thì lấy submission hiện có
    async function ensureDraftSubmission(exerciseId) {
        const cached = submissionByExercise[exerciseId];
        // ⬇️ Chỉ dùng cache nếu nó là DRAFT; nếu COMPLETED/GRADED thì tạo DRAFT mới
        if (cached && exIdOfSubmission(cached) === exerciseId && (cached.status === 'DRAFT')) {
            return cached;
        }

        // thử tìm DRAFT
        let sub = null;
        try {
            const found = await Apis.get(
                `${endpoints.submissions}/exercise/${exerciseId}`,
                { params: { studentId, status: 'DRAFT' } }
            );
            const arr = Array.isArray(found.data) ? found.data : (found.data?.items || []);
            sub = arr.find(x => exIdOfSubmission(x) === exerciseId) || null;
        } catch (_) { }

        // nếu không có thì tạo mới
        if (!sub) {
            const res = await Apis.post(
                `${endpoints.submissions}?exerciseId=${exerciseId}&studentId=${studentId}`,
                { status: 'DRAFT' }
            );
            sub = res.data;
        }

        setSubmissionByExercise(prev => ({ ...prev, [exerciseId]: sub }));
        return sub;
    }

    // Khi student mở 1 exercise → nạp submission + responses vào state
    const loadSubmissionState = useCallback(async (exerciseId) => {
        if (!isStudent || !studentId) return;
        try {
            const sub = await getExistingSubmission(exerciseId);
            if (!sub) return;
            setSubmissionByExercise(prev => ({ ...prev, [exerciseId]: sub }));
            await hydrateResponsesFromServer(sub.id);
        } catch (e) {
            console.warn('loadSubmissionState failed', e);
        }
    }, [isStudent, studentId]);

    // Flush tất cả câu trả lời hiện có của 1 exercise
    async function flushResponsesForExercise(exId, subId) {
        const qs = qState.get(exId)?.items || [];
        const tasks = [];

        for (const q of qs) {
            const qid = qidOf(q);
            if (qid == null) continue;

            // MCQ
            const ansId = selected[qid];
            if (ansId !== undefined && ansId !== null && String(ansId).trim() !== '') {
                const url = `${endpoints['mcq-responses']}/${subId}/${qid}?answerId=${encodeURIComponent(+ansId)}`;
                tasks.push(Apis.put(url));
            }

            // ESSAY: chỉ push nếu nội dung không rỗng
            if (Object.prototype.hasOwnProperty.call(essayDrafts, qid)) {
                const content = (essayDrafts[qid] ?? '').trim();
                if (content.length > 0) {
                    tasks.push(
                        Apis.put(`${endpoints['essay-responses']}/${subId}/${qid}`, { answerEssay: content })
                    );
                }
            }
        }

        if (tasks.length) await Promise.all(tasks);
    }

    // ===== Actions =====

    // LƯU BÀI (DRAFT)
    async function onSave(ex) {
        if (!isStudent || !studentId) {
            showError('Bạn cần đăng nhập tài khoản Student để lưu.');
            return;
        }
        setSaving(s => ({ ...s, [ex.id]: true }));
        try {
            const sub = await ensureDraftSubmission(ex.id);
            await flushResponsesForExercise(ex.id, sub.id);
            // optional: cập nhật timestamp DRAFT
            await Apis.put(
                `${endpoints.submissions}/${sub.id}?exerciseId=${ex.id}&studentId=${studentId}`,
                { status: 'DRAFT' }
            );
            showSuccess(`Đã lưu bài "${ex.title}".`);
        } catch (e) {
            console.error(e);
            showError('Lưu bài thất bại.');
        } finally {
            setSaving(s => ({ ...s, [ex.id]: false }));
        }
    }

    // NỘP BÀI (COMPLETED) – cho phép nộp nhiều lần
    async function onSubmit(ex) {
        if (!isStudent || !studentId) {
            showError('Bạn cần đăng nhập tài khoản Student để nộp.');
            return;
        }
        setSubmitting(s => ({ ...s, [ex.id]: true }));
        try {
            const sub = await ensureDraftSubmission(ex.id);
            await flushResponsesForExercise(ex.id, sub.id);
            const url = `${endpoints.submissions}/${sub.id}?exerciseId=${ex.id}&studentId=${studentId}`;
            const body = { status: 'COMPLETED', submittedAt: new Date().toISOString() };
            const res = await Apis.put(url, body);
            setSubmissionByExercise(prev => ({ ...prev, [ex.id]: res.data }));
            setGradeModal({
                open: true,
                exerciseId: ex.id,
                exerciseTitle: ex.title,
                submissionId: res.data?.id,
                initialGrade: res.data?.grade ?? null,
            });
            showSuccess(`Đã nộp bài "${ex.title}".`);
            await recalculateProgress();
        } catch (e) {
            console.error(e);
            showError('Nộp bài thất bại.');
        } finally {
            setSubmitting(s => ({ ...s, [ex.id]: false }));
        }
    }
    const recalculateProgress = async () => {
        if (role !== 'STUDENT' || !user?.id) return;
        try {
            const url = `${endpoints['chapter-progress']}/recalculate/${user.id}/${chapterId}`;
            console.log('Recalculating progress for:', url);
            const res = await authApis().post(url);
        } catch (err) {
            console.error('Error recalculating progress:', err.response?.data || err.message);
        }
    };

    // --- Nhóm MCQ / ESSAY ---
    const mcqExercises = useMemo(
        () => exercises.filter(ex => (ex.type || '').toUpperCase() === 'MCQ'),
        [exercises]
    );
    const essayExercises = useMemo(
        () => exercises.filter(ex => (ex.type || '').toUpperCase() === 'ESSAY'),
        [exercises]
    );

    const renderExerciseList = (list) => (
        <div className="ex-list">
            {list.map(ex => {
                const isOpen = expanded.has(ex.id);
                const q = qState.get(ex.id);
                const sub = submissionByExercise[ex.id];
                const isCompletedOrGraded = ['COMPLETED', 'GRADED'].includes(sub?.status); // ⬅️ cập nhật điều kiện

                return (
                    <div key={ex.id} className={`ex-item ${isOpen ? 'open' : ''}`}>
                        <button className="ex-toggle" onClick={() => toggle(ex.id)}>
                            <div className="ex-title">
                                <span className="ex-chevron">{isOpen ? '▾' : '▸'}</span>
                                <span className="ex-name">{ex.title}</span>
                                {isStudent && submissionByExercise[ex.id] &&
                                    submissionByExercise[ex.id].grade !== undefined &&
                                    submissionByExercise[ex.id].grade !== null && (
                                        <span className="ex-grade-badge" title="Điểm của bạn">
                                            {submissionByExercise[ex.id].grade}
                                        </span>
                                    )}
                            </div>
                            {canManage && (
                                <div className="ex-actions" onClick={(e) => e.stopPropagation()}>
                                    <button className="ex-icon-btn" title="Sửa" aria-label="Sửa" onClick={(e) => openEditExercise(ex, e)}>✏️</button>
                                    <button className="ex-icon-btn danger" title="Xóa" aria-label="Xóa" onClick={(e) => deleteExercise(ex.id, e)}>🗑️</button>
                                </div>
                            )}
                            <div className={`ex-type ex-type-${(ex.type || '').toLowerCase()}`}>{ex.type}</div>
                        </button>

                        {isOpen && (
                            <div className="ex-panel">
                                {q?.loading && <div className="ex-skeleton">Đang tải câu hỏi…</div>}

                                {canManage && (
                                    <div className="ex-actions-addQuestion">
                                        <button className="ex-add-btn" onClick={() => openCreateQuestion(ex.id)}>Thêm câu hỏi</button>
                                    </div>
                                )}

                                {q?.error && <div className="ex-error">{q.error}</div>}

                                {!q?.loading && !q?.error && (q?.items?.length ? (
                                    <ul className="ex-questions">
                                        {q.items.map((it) => {
                                            const qid = qidOf(it);
                                            const showSolution = !!it.solution && (canManage || (isStudent && isCompletedOrGraded));
                                            return (
                                                <li key={qid} className="ex-question">
                                                    <div className="ex-qheader">
                                                        <div className="ex-qline">
                                                            <span className="ex-qindex">{it.orderIndex}.</span>
                                                            <span className="ex-qtext">{it.question}</span>
                                                        </div>

                                                        {showSolution && (
                                                            <div className="ex-solution-text" style={{ marginTop: 8, background: '#f8fafc', border: '1px solid #e5e7eb', borderRadius: 8, padding: 8 }}>
                                                                <strong>Lời giải:</strong>
                                                                <div style={{ marginTop: 4, whiteSpace: 'pre-wrap' }}>{it.solution}</div>
                                                            </div>
                                                        )}

                                                        {canManage && (
                                                            <div className="ex-qactions" onClick={(e) => e.stopPropagation()}>
                                                                <button className="ex-icon-btn" title="Sửa" aria-label="Sửa"
                                                                    onClick={(e) => openEditQuestion(ex.id, it, e)}>✏️</button>
                                                                <button className="ex-icon-btn danger" title="Xóa" aria-label="Xóa"
                                                                    onClick={(e) => deleteQuestion(ex.id, it.id, e)}>🗑️</button>
                                                            </div>
                                                        )}
                                                    </div>

                                                    {(ex.type || '').toUpperCase() === 'MCQ' ? (
                                                        <AnswerSection
                                                            className="ex-answers-under"
                                                            questionId={qid}
                                                            selectedAnswerId={selected[qid]}
                                                            onSelect={(answerId) =>
                                                                setSelected(prev => ({ ...prev, [qid]: answerId }))
                                                            }
                                                            canManage={canManage}
                                                        />
                                                    ) : (
                                                        // ESSAY: chỉ Student mới thấy ô nhập (kể cả đã nộp, vì cho phép nộp lại)
                                                        (isStudent) && (
                                                            <div className="ex-essay-form">
                                                                <textarea
                                                                    id={`essay-${qid}`}
                                                                    className="ex-essay-input"
                                                                    rows={6}
                                                                    placeholder="Nhập câu trả lời tự luận…"
                                                                    value={essayDrafts[qid] || ''}
                                                                    onChange={(e) =>
                                                                        setEssayDrafts(prev => ({ ...prev, [qid]: e.target.value }))
                                                                    }
                                                                />
                                                            </div>
                                                        )
                                                    )}
                                                </li>
                                            );
                                        })}
                                    </ul>
                                ) : (
                                    <div className="ex-empty-qs">Chưa có câu hỏi.</div>
                                ))}

                                {/* Nút Lưu bài / Nộp bài cho từng exercise (chỉ Student) */}
                                {isStudent && (
                                    <div
                                        className="ex-submit-row"
                                        style={{ display: 'flex', justifyContent: 'center', gap: 12, marginTop: 16 }}
                                    >
                                        <button
                                            className="ex-save-btn"
                                            onClick={() => onSave(ex)}
                                            disabled={!!saving[ex.id]}          // ⬅️ KHÔNG khóa theo COMPLETED/GRADED
                                            style={{ minWidth: 120 }}
                                        >
                                            {saving[ex.id] ? 'Đang lưu...' : 'Lưu bài'}
                                        </button>
                                        <button
                                            className="ex-submit-btn"
                                            onClick={() => onSubmit(ex)}
                                            disabled={!!submitting[ex.id]}       // ⬅️ KHÔNG khóa theo COMPLETED/GRADED
                                            style={{ minWidth: 120 }}
                                        >
                                            {submitting[ex.id] ? 'Đang nộp...' : 'Nộp bài'}  {/* ⬅️ Bỏ nhãn “Đã nộp” */}
                                        </button>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                );
            })}
        </div>
    );

    return (
        <div
            className="ex-page"
            style={{ '--content-left': `${leftOffset}px`, '--content-top': `${topOffset}px` }}
        >
            <div className="ex-section">
                <div className="ex-header">
                    <h2>Bài tập chương</h2>
                    {canManage && (
                        <button className="ex-add-btn" onClick={openCreateExercise}>+ Thêm bài tập</button>
                    )}
                </div>

                {loading && <div className="ex-skeleton">Đang tải bài tập…</div>}

                {!loading && exercises.length === 0 && (
                    <div className="ex-empty">Chưa có bài tập trong chương này.</div>
                )}

                {/* --- Nhóm MCQ --- */}
                {!loading && mcqExercises.length > 0 && (
                    <div className="ex-group">
                        <div className="ex-group-header">
                            <h3 className="ex-group-title">Trắc nghiệm (MCQ)</h3>
                        </div>
                        {renderExerciseList(mcqExercises)}
                    </div>
                )}

                {/* --- Nhóm ESSAY --- */}
                {!loading && essayExercises.length > 0 && (
                    <div className="ex-group">
                        <div className="ex-group-header">
                            <h3 className="ex-group-title">Tự luận (ESSAY)</h3>
                        </div>
                        {renderExerciseList(essayExercises)}
                    </div>
                )}
                {/* Modals */}
                <ModalExercise
                    open={openExModal}
                    onClose={() => setOpenExModal(false)}
                    chapterId={chapterId}
                    initial={editingEx}
                    onSaved={reloadExercises}
                />
                <ModalQuestion
                    open={openQModal}
                    onClose={() => setOpenQModal(false)}
                    exerciseId={currentExId}
                    initial={editingQ}
                    onSaved={() => currentExId && fetchQuestions(currentExId)}
                />
                <ShowGrade
                    open={gradeModal.open}
                    onClose={() => setGradeModal({ open: false })}
                    exerciseId={gradeModal.exerciseId}
                    exerciseTitle={gradeModal.exerciseTitle}
                    submissionId={gradeModal.submissionId}
                    initialGrade={gradeModal.initialGrade}
                />
            </div>
        </div>
    );
};

export default ExerciseSection;
