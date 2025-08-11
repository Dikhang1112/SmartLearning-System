// components/ExerciseSection.js
import React, { useEffect, useState, useCallback } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import { showError } from '../utils/toast';
import '../static/exerciseSection.css';
import AnswerSection from './AnswerSection';

const ExerciseSection = ({ chapterId, role = 'STUDENT' }) => {
    const [loading, setLoading] = useState(false);
    const [exercises, setExercises] = useState([]);
    const [expanded, setExpanded] = useState(new Set());
    const [qState, setQState] = useState(new Map());
    const [selected, setSelected] = useState({}); // { [questionId]: answerId }

    const canManage = role === 'TEACHER';

    useEffect(() => {
        let cancel = false;
        const load = async () => {
            setLoading(true);
            try {
                const res = await Apis.get(`${endpoints.excercises}/chapter/${chapterId}`);
                if (!cancel) setExercises(res.data?.items || []);
            } catch (e) {
                if (!cancel) showError('Không tải được danh sách bài tập');
            } finally {
                if (!cancel) setLoading(false);
            }
        };
        if (chapterId) load();
        return () => { cancel = true; };
    }, [chapterId]);

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

    return (
        <div className="ex-section">
            <div className="ex-header">
                <h2>Bài tập chương </h2>
            </div>

            {loading && <div className="ex-skeleton">Đang tải bài tập…</div>}

            {!loading && exercises.length === 0 && (
                <div className="ex-empty">Chưa có bài tập trong chương này.</div>
            )}

            <div className="ex-list">
                {exercises.map(ex => {
                    const isOpen = expanded.has(ex.id);
                    const q = qState.get(ex.id);
                    return (
                        <div key={ex.id} className={`ex-item ${isOpen ? 'open' : ''}`}>
                            <button className="ex-toggle" onClick={() => toggle(ex.id)}>
                                <div className="ex-title">
                                    <span className="ex-chevron">{isOpen ? '▾' : '▸'}</span>
                                    <span className="ex-name">{ex.title}</span>
                                </div>
                                <div className={`ex-type ex-type-${(ex.type || '').toLowerCase()}`}>{ex.type}</div>
                            </button>

                            {isOpen && (
                                <div className="ex-panel">
                                    {q?.loading && <div className="ex-skeleton">Đang tải câu hỏi…</div>}
                                    {q?.error && <div className="ex-error">{q.error}</div>}

                                    {!q?.loading && !q?.error && (q?.items?.length ? (
                                        <ul className="ex-questions">
                                            {q.items.map((it) => (
                                                <li key={it.id} className="ex-question">
                                                    {/* Hàng đầu: số thứ tự + text + (nút) Xem lời giải ở cùng hàng */}
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
                                                    </div>

                                                    {/* Hàng dưới: đáp án inline */}
                                                    <AnswerSection
                                                        className="ex-answers-under"
                                                        questionId={it.id}
                                                        selectedAnswerId={selected[it.id]}
                                                        onSelect={(answerId) =>
                                                            setSelected(prev => ({ ...prev, [it.id]: answerId }))
                                                        }
                                                    // showCorrect={true}
                                                    />
                                                </li>
                                            ))}
                                        </ul>
                                    ) : (
                                        <div className="ex-empty-qs">Chưa có câu hỏi.</div>
                                    ))}

                                    {canManage && (
                                        <div className="ex-actions">
                                            <button className="ex-btn">Thêm câu hỏi</button>
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default ExerciseSection;
