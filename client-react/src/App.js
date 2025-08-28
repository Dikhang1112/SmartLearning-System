import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useReducer, useEffect } from 'react';
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { CookiesProvider } from 'react-cookie';
import Header from './components/layouts/Header';
import Welcome from './components/layouts/Welcome';
import Sidebar from './components/layouts/Sidebar';
import MyUserReducer, { MyUserContext, MyUserDispatchContext } from './reducers/MyUserReducer';
import { SidebarProvider } from './reducers/SidebarContext';
import StudentDashboard from './components/StudentDashboard';
import TeacherDashboard from './components/TeacherDashboard';
import ChapterSection from './components/ChapterSection';
import StudyPlans from './components/StudyPlans';
import Submission from './components/Submission';
import Chapter from './components/Chapter';
import SubmissionChapter from './components/SubmissionChapter';
import { endpoints, authApis } from './configs/Apis';
import cookie from 'react-cookies';
import 'react-toastify/dist/ReactToastify.css';
import { ToastContainer } from 'react-toastify';

function App() {
  const [user, dispatch] = useReducer(MyUserReducer, null);
  useEffect(() => {
    const init = async () => {
      let token = localStorage.getItem("token") || cookie.load("token");
      if (token) {
        try {
          const res = await authApis().get(endpoints.auth);
          const currentUser = {
            id: res.data.id,
            token: token,
            email: res.data.email,
            name: res.data.name,
            role: res.data.role,
            avatar: res.data.avatar
          }
          dispatch({
            type: "login" || "loginGoogle",
            payload: currentUser
          });
        } catch (err) {
          console.error("Token lỗi hoặc hết hạn:", err);
          localStorage.removeItem("token");
          cookie.remove("token");
        }
      }
    };
    init();
  }, []); // ĐỂ MẢNG RỖNG
  return (
    <MyUserContext.Provider value={user}>
      <CookiesProvider>
        <MyUserDispatchContext.Provider value={dispatch}>
          <SidebarProvider>
            <BrowserRouter>
              <Header />
              <Sidebar />
              <Routes>
                <Route path="/" element={<Welcome />} />
                <Route path="/studentDashBoard/" element={<StudentDashboard />} />
                <Route path="/teacherDashBoard/" element={<TeacherDashboard />} />
                <Route path="/chapters/:subjectId" element={<Chapter />} />
                <Route path="/chapters/:subjectId/section/:chapterId" element={<ChapterSection />} />
                <Route path="/studyPlans" element={<StudyPlans />} />
                <Route path="/submission" element={<Submission />} />
                <Route path="/submission/chapters" element={<SubmissionChapter />} />
              </Routes>
              <ToastContainer // Toast toàn cục
                position="top-right"
                autoClose={3000}
                hideProgressBar={false}
                newestOnTop={true}
                closeOnClick
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
              />
            </BrowserRouter>
          </SidebarProvider>
        </MyUserDispatchContext.Provider>
      </CookiesProvider>
    </MyUserContext.Provider>
  );
}
export default App;
