// components/ExerciseSection.js
import React, { useEffect, useState, useCallback, useMemo } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import '../static/exerciseSection.css';
import AnswerSection from './AnswerSection';
import ModalExercise from './ModalExercise';
import { showSuccess, showError } from '../utils/toast';
import ModalQuestion from './ModalQuestion';

const ExerciseSection = ({ chapterId, role = 'TEACHER' }) => {
    const [loading, setLoading] = useState(false);
    const [exercises, setExercises] = useState([]);
    const [expanded, setExpanded] = useState(new Set());
    const [qState, setQState] = useState(new Map());
    const [selected, setSelected] = useState({}); // { [questionId]: answerId }
    const canManage = role === 'TEACHER';
    const [openExModal, setOpenExModal] = useState(false);
    const [editingEx, setEditingEx] = useState(null);
    const [openQModal, setOpenQModal] = useState(false);
    const [editingQ, setEditingQ] = useState(null);
    const [currentExId, setCurrentExId] = useState(null);
    const [essayDrafts, setEssayDrafts] = useState({});

    // tải danh sách bài tập
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
                return (
                    <div key={ex.id} className={`ex-item ${isOpen ? 'open' : ''}`}>
                        <button className="ex-toggle" onClick={() => toggle(ex.id)}>
                            <div className="ex-title">
                                <span className="ex-chevron">{isOpen ? '▾' : '▸'}</span>
                                <span className="ex-name">{ex.title}</span>
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
                                        {q.items.map((it) => (
                                            <li key={it.id} className="ex-question">
                                                <div className="ex-qheader">
                                                    <div className="ex-qline">
                                                        <span className="ex-qindex">{it.orderIndex}.</span>
                                                        <span className="ex-qtext">{it.question}</span>
                                                    </div>
                                                    {it.solution && (
                                                        <details className="ex-solution-inline">
                                                            <summary>Xem lời giải</summary>
                                                            <div className="ex-solution-text">{it.solution}</div>
                                                        </details>
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
                                                        questionId={it.id}
                                                        selectedAnswerId={selected[it.id]}
                                                        onSelect={(answerId) =>
                                                            setSelected(prev => ({ ...prev, [it.id]: answerId }))
                                                        }
                                                        canManage={canManage}
                                                    />
                                                ) : (
                                                    // ESSAY
                                                    !canManage && (
                                                        <div className="ex-essay-form">
                                                            <label htmlFor={`essay-${it.id}`} className="ex-essay-label">
                                                                Câu trả lời của bạn
                                                            </label>
                                                            <textarea
                                                                id={`essay-${it.id}`}
                                                                className="ex-essay-input"
                                                                rows={6}
                                                                placeholder="Nhập câu trả lời tự luận…"
                                                                value={essayDrafts[it.id] || ''}
                                                                onChange={(e) =>
                                                                    setEssayDrafts(prev => ({ ...prev, [it.id]: e.target.value }))
                                                                }
                                                            />
                                                        </div>
                                                    )
                                                )}
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <div className="ex-empty-qs">Chưa có câu hỏi.</div>
                                ))}
                            </div>
                        )}
                    </div>
                );
            })}
        </div>
    );

    return (
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
                        <span className="ex-group-badge ex-type-mcq">MCQ</span>
                    </div>
                    {renderExerciseList(mcqExercises)}
                </div>
            )}

            {/* --- Nhóm ESSAY --- */}
            {!loading && essayExercises.length > 0 && (
                <div className="ex-group">
                    <div className="ex-group-header">
                        <h3 className="ex-group-title">Tự luận (ESSAY)</h3>
                        <span className="ex-group-badge ex-type-essay">ESSAY</span>
                    </div>
                    {renderExerciseList(essayExercises)}
                </div>
            )}

            {/* Nút Nộp bài cho role STUDENT (chỉ hiển thị) */}
            {!canManage && exercises.length > 0 && (
                <div className="ex-submit-bar">
                    <button className="ex-submit-btn" type="button">Nộp bài</button>
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
        </div>
    );
};

export default ExerciseSection;
