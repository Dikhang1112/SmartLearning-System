import React, { useContext, useEffect, useState } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import { useParams, useNavigate } from 'react-router-dom'; // ⬅ thêm useNavigate
import { SidebarContext } from '../reducers/SidebarContext';
import '../static/chapter.css';

const Chapter = () => {
    const { subjectId } = useParams();
    const navigate = useNavigate(); // ⬅ hook điều hướng
    const [subject, setSubject] = useState(null);
    const [chapters, setChapters] = useState([]);
    const [loading, setLoading] = useState(true);
    const { collapsed } = useContext(SidebarContext);

    useEffect(() => {
        const loadData = async () => {
            setLoading(true);
            try {
                const resSubject = await Apis.get(`${endpoints.subjects}/${subjectId}`);
                setSubject(resSubject.data);

                const resChapters = await Apis.get(`${endpoints.chapters}/${subjectId}`);
                setChapters(resChapters.data);
            } catch (err) {
                setSubject(null);
                setChapters([]);
                console.error('Error loading chapter data:', err);
            }
            setLoading(false);
        };

        if (subjectId) loadData();
    }, [subjectId]);

    const goToSection = (chapterId) => {
        navigate(`/chapters/${subjectId}/section/${chapterId}`);
    };

    return (
        <div className="chapter-main" style={{ paddingLeft: collapsed ? '80px' : '300px' }}>
            {loading ? (
                <div>Đang tải...</div>
            ) : (
                subject && (
                    <div className="chapter-header">
                        <div className="chapter-header-left">
                            <h1 className="chapter-title">{subject.title}</h1>
                            <p className="chapter-desc">{subject.description}</p>
                        </div>
                        <div className="chapter-header-img">
                            <img src={subject.image} alt={subject.title} />
                        </div>
                    </div>
                )
            )}

            <div>
                <h2 className="chapter-section-title">Các chương</h2>
                <div className="chapter-list">
                    {loading && <div>Đang tải danh sách chương...</div>}
                    {!loading && chapters.length === 0 && (
                        <div className="chapter-empty">Chưa có chương nào.</div>
                    )}
                    {!loading && chapters.map((ch, idx) => (
                        <div
                            className="chapter-card"
                            key={ch.id || idx}
                            onClick={() => goToSection(ch.id)} // ⬅ click để navigate
                            style={{ cursor: 'pointer' }} // ⬅ đổi con trỏ để user biết có thể click
                        >
                            <div className="chapter-indexbox">
                                <div>{ch.orderIndex.toString().padStart(2, '0')}</div>
                                <div className="chapter-lesson-text">LESSON</div>
                            </div>
                            <div className="chapter-info">
                                <div className="chapter-card-title">{ch.title}</div>
                                <div className="chapter-card-sumary">{ch.summaryText}</div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};
export default Chapter;
