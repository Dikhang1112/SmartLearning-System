import React, { useState, useContext } from 'react';
import '../static/login.css';
import Apis, { endpoints, authApis } from '../configs/Apis';
import { MyUserDispatchContext } from '../reducers/MyUserReducer';
import { MyUserContext } from '../reducers/MyUserReducer';
import { useNavigate } from 'react-router-dom';
import cookie from 'react-cookies';
import AuthGoogle from '../configs/AuthGoogle';

const Login = ({ onLoginSuccess }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const dispatch = useContext(MyUserDispatchContext);
    const user = useContext(MyUserContext);
    const role = user?.role || 'STUDENT'; // mặc định readonly nếu chưa đăng nhập
    const canManage = role === 'TEACHER';
    const nav = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        // Validate input
        if (!email || !password) {
            setError('Vui lòng nhập email và mật khẩu.');
            setLoading(false);
            return;
        }

        try {
            const res = await Apis.post(endpoints['login'], {
                email,
                password,
            });
            const token = res.data.token;
            if (!token) {
                throw new Error('Không có token trong phản hồi');
            } else {
                cookie.save("token", token);
                localStorage.setItem('token', token);
            }
            const currentUser = await authApis().get(endpoints.auth);
            const userInfo = currentUser.data;
            if (res.data && res.data.token) {
                setSuccess('Đăng nhập thành công!');
                setError('');
                dispatch({
                    type: "login",
                    payload: {
                        token: token,
                        email: userInfo.email,
                        name: userInfo.name,
                        role: userInfo.role,
                        id: userInfo.id,
                        avatar: userInfo.avatar
                    }
                });
                if (onLoginSuccess) onLoginSuccess(); // Tắt modal khi login thành công!
                if (userInfo.role === "STUDENT") {
                    nav('/studentDashboard');
                }
                else if (userInfo.role === "TEACHER") {
                    nav('/teacherDashboard');
                }
            } else {
                setError('Đăng nhập thất bại. Vui lòng kiểm tra lại.');
                setSuccess('');
            }
        } catch (err) {
            if (err.response && err.response.status === 400) {
                setError('Email hoặc mật khẩu không đúng!');
            } else {
                setError('Có lỗi xảy ra. Vui lòng thử lại sau.');
            }
            setSuccess('');
        } finally {
            setLoading(false);
        }
    };

    const handleGoogleLoginSuccess = async (token) => {
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            console.log("JWT token for profile call: ", token); // Log JWT trước khi gọi profile
            const currentUser = await authApis().get(endpoints.auth); // Sửa endpoint thành endpoints.auth (/auth/user)
            const userInfo = currentUser.data;
            dispatch({
                type: "loginGoogle",
                payload: {
                    token: token,
                    email: userInfo.email,
                    name: userInfo.name,
                    role: userInfo.role,
                    id: userInfo.id,
                    avatar: userInfo.avatar
                }
            });
            setSuccess('Đăng nhập bằng Google thành công!');
            setError('');
            if (onLoginSuccess) onLoginSuccess(); // Tắt modal khi login thành công
            if (userInfo.role === "STUDENT") {
                nav('/studentDashboard');
            } else if (userInfo.role === "TEACHER") {
                nav('/teacherDashboard');
            }
        } catch (err) {
            console.error("Profile fetch error after Google login: ", err); // Log lỗi gọi profile
            setError('Có lỗi xảy ra sau khi đăng nhập bằng Google. Vui lòng thử lại.');
            setSuccess('');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-title">Đăng nhập</div>
            <form className="login-form" onSubmit={handleSubmit}>
                <div className="input-group">
                    <label htmlFor="email">Email</label>
                    <input
                        type="email"
                        id="email"
                        className="login-input"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                        autoFocus
                    />
                </div>
                <div className="input-group">
                    <label htmlFor="password">Mật khẩu</label>
                    <input
                        type="password"
                        id="password"
                        className="login-input"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                    />
                </div>
                {error && <div className="login-error">{error}</div>}
                {success && <div className="login-success">{success}</div>}
                <AuthGoogle onLoginSuccess={handleGoogleLoginSuccess} />
                <div className="login-actions">
                    <button type="submit" className="login-btn" disabled={loading}>
                        {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
                    </button>
                </div>
            </form>
        </div>
    );
};
export default Login;