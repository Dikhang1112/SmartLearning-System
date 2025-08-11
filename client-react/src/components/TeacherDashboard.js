import React, { useContext, useState, useEffect } from 'react'
import { MyUserContext } from '../reducers/MyUserReducer';
import { SidebarContext } from '../reducers/SidebarContext';
import Apis, { endpoints } from '../configs/Apis';
import '../static/teacherDashboard.css';
import { useNavigate } from "react-router-dom";

const TeacherDashboard = () => {
    const user = useContext(MyUserContext);
    const [subjects, setSubjects] = useState([]);
    const [classNames, setClassNames] = useState([]);            // Các lớp của giáo viên
    const [subjectClassMap, setSubjectClassMap] = useState({});   // subjectId -> [className,...]
    const [loading, setLoading] = useState(true);
    const [loadingClasses, setLoadingClasses] = useState(true);
    const { collapsed } = useContext(SidebarContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (!user?.id) return;

        const fetchSubjects = async () => {
            setLoading(true);
            try {
                const response = await Apis.get(`${endpoints.teachers}/${user.id}`);
                setSubjects(response.data.subjectList || []);
            } catch (error) {
                setSubjects([]);
                console.error("Error fetching teacher subjects:", error);
            } finally {
                setLoading(false);
            }
        };

        const fetchClasses = async () => {
            setLoadingClasses(true);
            try {
                const res = await Apis.get(endpoints.classes);
                let classesData = res.data;

                // /classes có thể trả về 1 object hoặc 1 mảng => chuẩn hoá thành mảng
                if (!Array.isArray(classesData)) classesData = [classesData];

                const myClasses = [];
                const mySubjectMap = {}; // { [subjectId]: Set<className> }

                classesData.forEach(c => {
                    const teacherList = Array.isArray(c.teacherList) ? c.teacherList : [];
                    teacherList.forEach(t => {
                        if (t?.userId === user.id) {
                            // Ghi nhận lớp của giáo viên
                            if (c?.className && !myClasses.includes(c.className)) {
                                myClasses.push(c.className);
                            }
                            // Map subject -> className
                            const subList = Array.isArray(t.subjectList) ? t.subjectList : [];
                            subList.forEach(s => {
                                if (s?.id != null) {
                                    if (!mySubjectMap[s.id]) mySubjectMap[s.id] = new Set();
                                    if (c?.className) mySubjectMap[s.id].add(c.className);
                                }
                            });
                        }
                    });
                });

                // Chuyển Set -> Array để render
                const mapAsArray = Object.fromEntries(
                    Object.entries(mySubjectMap).map(([k, v]) => [k, Array.from(v)])
                );

                setClassNames(myClasses);
                setSubjectClassMap(mapAsArray);
            } catch (err) {
                console.error("Error fetching classes:", err);
                setClassNames([]);
                setSubjectClassMap({});
            } finally {
                setLoadingClasses(false);
            }
        };

        // gọi song song
        fetchSubjects();
        fetchClasses();
    }, [user?.id]);

    const handleSubjectClick = (subjectId) => {
        navigate(`/chapters/${subjectId}`);
    };

    return (
        <div className="main-content" style={{ paddingLeft: collapsed ? '80px' : '300px' }}>
            <section className="welcome-section">
                <div className="avatar-wrapper">
                    <img
                        src={user?.avatar || "/default-avatar.png"}
                        alt="Avatar"
                        className="user-avatar"
                    />
                </div>
                <span className="welcome-text">
                    Xin chào, <b>{user?.name || "Giáo viên"}</b> chào mừng bạn đến với hệ thống SmartStudy!
                </span>
            </section>
            {/* Phần Môn học */}
            <section className="subject-section">
                <h2 className="section-list">Các môn học bạn đang giảng dạy</h2>
                <div className="subject-list">
                    {loading && <p>Đang tải dữ liệu môn học...</p>}
                    {!loading && subjects.length === 0 && <p>Bạn chưa được phân công môn học nào.</p>}
                    {subjects.map(subject => {
                        const classesForSubject = subjectClassMap[subject.id] || [];
                        return (
                            <div
                                key={subject.id}
                                className="subject-card"
                                style={{ cursor: 'pointer' }}
                                onClick={() => handleSubjectClick(subject.id)}
                            >
                                <div className="subject-card-imgbox">
                                    <img src={subject.image} alt={subject.title} className="subject-card-img-full" />
                                </div>
                                <div className="subject-card-body">
                                    <div className="subject-card-title" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                                        <span>{subject.title}</span>
                                    </div>
                                    <div className="subject-card-teacher">
                                        Lớp: {classesForSubject.length ? classesForSubject.join(', ') : '—'}
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            </section>
        </div>
    );
};

export default TeacherDashboard;
