// components/AnswerSection.js
import React, { useEffect, useState } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import '../static/answerSection.css';

const AnswerSection = ({ questionId, selectedAnswerId, onSelect, showCorrect = false, className = '' }) => {
    const [loading, setLoading] = useState(false);
    const [answers, setAnswers] = useState([]);
    const [error, setError] = useState(null);

    useEffect(() => {
        let cancel = false;
        const load = async () => {
            setLoading(true);
            try {
                const url = endpoints.answersByQuestion(questionId);
                const res = await Apis.get(url, { params: { sort: 'id', dir: 'ASC', page: 0, size: 100 } });
                if (!cancel) {
                    setAnswers(res.data?.items || []);
                    setError(null);
                }
            } catch {
                if (!cancel) setError('Không tải được đáp án');
            } finally {
                if (!cancel) setLoading(false);
            }
        };
        if (questionId) load();
        return () => { cancel = true; };
    }, [questionId]);

    if (loading) return <div className="ans-skeleton">Đang tải đáp án…</div>;
    if (error) return <div className="ans-error">{error}</div>;
    if (!answers.length) return <div className="ans-empty">Chưa có đáp án.</div>;

    return (
        <div className={`ans-inline ${className}`}>
            {answers.map((a, idx) => {
                const selected = selectedAnswerId === a.id;
                const correct = showCorrect && a.isCorrect === true;
                return (
                    <button
                        key={a.id}
                        type="button"
                        className={`ans-chip ${selected ? 'selected' : ''} ${correct ? 'correct' : ''}`}
                        onClick={() => onSelect?.(a.id, a)}
                    >
                        <span className="ans-letter">{String.fromCharCode(65 + idx)}</span>
                        <span className="ans-text">{a.answerText}</span>
                    </button>
                );
            })}
        </div>
    );
};

export default AnswerSection;
