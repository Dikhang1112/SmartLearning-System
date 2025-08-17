// components/Chapter.js
import React, { useContext, useEffect, useState } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import { useParams, useNavigate } from 'react-router-dom';
import { SidebarContext } from '../reducers/SidebarContext';
import { MyUserContext } from '../reducers/MyUserReducer';
import '../static/chapter.css';
import { showSuccess, showError } from '../utils/toast';
import ModalChapter from './ModalChapter';

const Chapter = () => {
    const { subjectId } = useParams();
    const navigate = useNavigate();
    const { collapsed } = useContext(SidebarContext);
    const [subject, setSubject] = useState(null);
    const [chapters, setChapters] = useState([]);
    const [loading, setLoading] = useState(true);
    // modal state
    const [openModal, setOpenModal] = useState(false);
    const [editing, setEditing] = useState(null);
    const user = useContext(MyUserContext);
    const role = user?.role || 'STUDENT'; // mặc định readonly nếu chưa đăng nhập
    const canManage = role === 'TEACHER';

    const reload = async () => {
        setLoading(true);
        try {
            const resSubject = await Apis.get(`${endpoints.subjects}/${subjectId}`);
            setSubject(resSubject.data);

            // Backend của bạn đang trả mảng trực tiếp:
            const resChapters = await Apis.get(`${endpoints.chapters}/subject/${subjectId}`);
            setChapters(resChapters.data || []);
        } catch (err) {
            setSubject(null);
            setChapters([]);
            console.error('Error loading chapter data:', err);
        }
        setLoading(false);
    };

    useEffect(() => {
        if (subjectId) reload();
        // eslint-disable-next-line
    }, [subjectId]);

    const goToSection = (chapterId) => {
        navigate(`/chapters/${subjectId}/section/${chapterId}`);
    };

    // open create modal
    const openCreate = () => {
        if (!canManage) return; // chặn nếu không có quyền
        setEditing(null);
        setOpenModal(true);
    };

    // open edit modal
    const openEdit = (ch, e) => {
        if (!canManage) return; // chặn nếu không có quyền
        e.stopPropagation();
        setEditing(ch);
        setOpenModal(true);
    };

    const onDelete = async (id, e) => {
        if (!canManage) return; // chặn nếu không có quyền
        e.stopPropagation();
        if (!window.confirm('Bạn có chắc muốn xóa chương này?')) return;
        try {
            await Apis.delete(`${endpoints.chapters}/${id}`);
            showSuccess('Xóa chương thành công.');
        } catch (err) {
            console.error(err);
            showError('Xóa chương thất bại. Vui lòng thử lại sau.');
        }
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

            <div className="chapter-toolbar">
                <h2 className="chapter-section-title">Các chương</h2>
                {canManage && (
                    <button className="chapter-add-btn" onClick={openCreate}>+ Thêm chương</button>
                )}
            </div>
            <div className="chapter-list">
                {loading && <div>Đang tải danh sách chương...</div>}
                {!loading && chapters.length === 0 && (
                    <div className="chapter-empty">Chưa có chương nào.</div>
                )}
                {!loading && chapters.map((ch) => (
                    <div
                        className="chapter-card"
                        key={ch.id}
                        onClick={() => goToSection(ch.id)}
                        style={{ cursor: 'pointer', position: 'relative' }}
                    >
                        {/* Actions for teacher (edit/delete) */}
                        {canManage && (
                            <div className="chapter-actions" onClick={(e) => e.stopPropagation()}>
                                <button className="chapter-icon-btn" title="Sửa" aria-label="Sửa"
                                    onClick={(e) => openEdit(ch, e)}>
                                    ✏️ {/* Pen icon bằng Unicode */}
                                </button>

                                <button className="chapter-icon-btn danger" title="Xóa" aria-label="Xóa"
                                    onClick={(e) => onDelete(ch.id, e)}>
                                    🗑️ {/* Bin icon bằng Unicode */}
                                </button>
                            </div>
                        )}
                        <div className="chapter-indexbox">
                            <div>{String(ch.orderIndex).padStart(2, '0')}</div>
                            <div className="chapter-lesson-text">LESSON</div>
                        </div>
                        <div className="chapter-info">
                            <div className="chapter-card-title">{ch.title}</div>
                            <div className="chapter-card-sumary">{ch.summaryText}</div>
                        </div>
                    </div>
                ))}
            </div>

            {/* Modal tạo/sửa Chapter */}
            <ModalChapter
                open={openModal}
                onClose={() => setOpenModal(false)}
                subjectId={parseInt(subjectId, 10)}
                initial={editing}
                onSaved={reload}
            />
        </div>
    );
};

export default Chapter;
