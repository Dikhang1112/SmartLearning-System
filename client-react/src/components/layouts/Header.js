import React, { useContext, useState } from 'react';
import { MyUserContext, MyUserDispatchContext } from '../../reducers/MyUserReducer';
import { useNavigate } from 'react-router-dom';
import logo from '../../asset/img/logo.png';
import '../../static/header.css';
// import Login and SignUp components
import Login from '../../components/Login';
import SignUp from '../../components/SignUp';

const Header = () => {
  const user = useContext(MyUserContext);
  const navigate = useNavigate();
  const dispatch = useContext(MyUserDispatchContext);
  const nav = useNavigate();
  // State for modal visibility
  const [showLogin, setShowLogin] = useState(false);
  const [showSignUp, setShowSignUp] = useState(false);

  // Close modal handler
  const handleCloseModal = () => {
    setShowLogin(false);
    setShowSignUp(false);
  };
  const handleLogout = () => {
    dispatch({ type: 'logout' });
    nav('/');
    setShowLogin(false);
    setShowSignUp(false)
  };

  return (
    <>
      <nav className="navbar navbar-expand-lg navbar-light bg-light shadow-sm custom-header">
        <div className="header-container d-flex w-100 align-items-center justify-content-between">
          <div className="d-flex align-items-center">
            <img
              src={logo}
              alt="StudySmart Logo"
              className="header-logo"
            />
            <span className="navbar-brand mb-0 h1">StudySmart</span>
          </div>
          <div className="d-flex ms-auto">
            {user ? (
              <button className="btn btn-outline-danger" onClick={handleLogout}>
                Đăng xuất
              </button>
            ) : (
              <>
                <button className="btn btn-outline-primary me-2" onClick={() => setShowLogin(true)}>
                  Đăng nhập
                </button>
                <button className="btn btn-primary" onClick={() => setShowSignUp(true)}>
                  Đăng ký
                </button>
              </>
            )}
          </div>
        </div>
      </nav>

      {/* Modal Login */}
      {showLogin && (
        <div className="custom-modal" onClick={handleCloseModal}>
          <div className="custom-modal-content" onClick={e => e.stopPropagation()}>
            <button className="modal-close-btn" onClick={handleCloseModal}>&times;</button>
            {/* TRUYỀN CALLBACK cho Login */}
            <Login onLoginSuccess={() => setShowLogin(false)} />
          </div>
        </div>
      )}
      {/* Modal Sign Up */}
      {showSignUp && (
        <div className="custom-modal" onClick={handleCloseModal}>
          <div className="custom-modal-content" onClick={e => e.stopPropagation()}>
            <button className="modal-close-btn" onClick={handleCloseModal}>&times;</button>
            <SignUp />
          </div>
        </div>
      )}
    </>
  );
};
export default Header;
