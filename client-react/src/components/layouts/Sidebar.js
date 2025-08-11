import React, { useContext } from 'react'
import { MyUserContext } from '../../reducers/MyUserReducer'
import { FaChevronLeft, FaChevronRight, FaBook, FaCalendarAlt } from "react-icons/fa";
import { SidebarContext } from '../../reducers/SidebarContext'; // Đảm bảo path đúng!
import { useNavigate } from "react-router-dom";
import '../../static/sidebar.css';

const menus = [
    { icon: <FaBook />, title: "Môn học", path: "/studentDashboard" },
    { icon: <FaCalendarAlt />, title: "Kế hoạch học tập", path: "/studyPlans" }, // Cập nhật path nếu có route này
];

const Sidebar = () => {
    const user = useContext(MyUserContext);
    const { collapsed, setCollapsed } = useContext(SidebarContext);
    const navigate = useNavigate();

    if (!user) return null;

    // Hàm xử lý khi click menu
    const handleMenuClick = (path) => {
        if (path) navigate(path);
    }

    return (
        <div className={`sidebar${collapsed ? " collapsed" : ""}`}>
            <button className="sidebar-toggle" onClick={() => setCollapsed(!collapsed)}>
                {collapsed ? <FaChevronRight size={18} /> : <FaChevronLeft size={18} />}
            </button>
            <div className="sidebar-menu">
                {menus.map((item, idx) => (
                    <div
                        key={idx}
                        className="sidebar-item"
                        onClick={() => handleMenuClick(item.path)}
                        style={{ cursor: 'pointer' }}
                    >
                        <span className="sidebar-icon">{item.icon}</span>
                        {!collapsed && <span className="sidebar-label">{item.title}</span>}
                    </div>
                ))}
            </div>
        </div>
    );
};
export default Sidebar;
