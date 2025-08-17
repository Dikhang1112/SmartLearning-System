// components/ModalExercise.js
import React, { useEffect, useState } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import { showSuccess, showError } from '../utils/toast';
import '../static/modalChapter.css'; // tái dùng style modal

const TYPE_OPTIONS = ['MCQ', 'ESSAY'];

const ModalExercise = ({
    open,
    onClose,
    chapterId,     // gắn bài tập vào chapter hiện tại
    initial = null, // có id => edit, null => create
    onSaved,        // callback reload list sau khi lưu
}) => {
    const [form, setForm] = useState({
        title: '',
        description: '',
        type: 'MCQ'
    });
    const [saving, setSaving] = useState(false);
    const [err, setErr] = useState('');

    useEffect(() => {
        if (!open) return;
        if (initial) {
            setForm({
                title: initial.title ?? '',
                description: initial.description ?? '',
                type: (initial.type || 'MCQ').toUpperCase()
            });
        } else {
            setForm({ title: '', description: '', type: 'MCQ' });
        }
        setErr('');
    }, [open, initial]);

    if (!open) return null;

    const onChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const validate = () => {
        if (!form.title?.trim()) return 'Vui lòng nhập tiêu đề bài tập';
        const t = form.type?.toUpperCase();
        if (!TYPE_OPTIONS.includes(t)) return 'Loại bài tập không hợp lệ (MCQ/ESSAY)';
        return '';
    };

    const submit = async (e) => {
        e.preventDefault();
        const v = validate();
        if (v) { setErr(v); return; }

        setSaving(true);
        try {
            // Backend bạn nhận Exercise entity: gửi chapterId dạng object để bind quan hệ
            const payload = {
                title: form.title.trim(),
                description: form.description?.trim() || '',
                type: form.type.toUpperCase()
            };

            if (initial?.id) {
                await Apis.put(`${endpoints.excercises}/${initial.id}`, payload, { params: { chapterId } });
                showSuccess('Cập nhật bài tập thành công.');
            } else {
                await Apis.post(`${endpoints.excercises}`, payload, { params: { chapterId } });
                showSuccess('Tạo bài tập thành công.');
            }

            onSaved && onSaved();
            onClose && onClose();
        } catch (e) {
            console.error(e);
            setErr('Lưu bài tập thất bại. Vui lòng thử lại.');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="mc-overlay" onClick={onClose}>
            <div className="mc-dialog" onClick={(e) => e.stopPropagation()}>
                <div className="mc-header">
                    <h3>{initial ? 'Chỉnh sửa bài tập' : 'Thêm bài tập'}</h3>
                    <button className="mc-close" onClick={onClose} aria-label="Đóng">✕</button>
                </div>

                <form className="mc-form" onSubmit={submit}>
                    <div className="mc-field">
                        <label>Tiêu đề (title)</label>
                        <input
                            name="title"
                            type="text"
                            value={form.title}
                            onChange={onChange}
                            placeholder="VD: Bài tập về mệnh đề?"
                            required
                        />
                    </div>

                    <div className="mc-field">
                        <label>Mô tả (description)</label>
                        <textarea
                            name="description"
                            rows={4}
                            value={form.description}
                            onChange={onChange}
                            placeholder="Chọn câu trả lời đúng nhất cho mỗi câu…"
                        />
                    </div>

                    <div className="mc-field">
                        <label>Loại (type)</label>
                        <select
                            name="type"
                            value={form.type}
                            onChange={onChange}
                            required
                        >
                            {TYPE_OPTIONS.map(o => (
                                <option key={o} value={o}>{o}</option>
                            ))}
                        </select>
                    </div>

                    {err && <div className="mc-error">{err}</div>}

                    <div className="mc-actions">
                        <button type="button" className="mc-btn ghost" onClick={onClose}>Hủy</button>
                        <button type="submit" className="mc-btn primary" disabled={saving}>
                            {saving ? 'Đang lưu…' : (initial ? 'Cập nhật' : 'Tạo mới')}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ModalExercise;
