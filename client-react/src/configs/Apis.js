import axios from "axios";
import cookie from "react-cookies";
const BASE_URL = 'http://localhost:8080/javaSpring/api/';

export const endpoints = {
    'users': '/users',
    'login': '/login',
    'auth': '/auth/user',
    'authGoogle': '/google-login',
    'signup': '/users',
    'teachers': '/teachers',
    'students': '/students',
    'subjects': '/subjects',
    'classes': '/classes',
    'chapters': '/chapters',
    'excercises': '/exercises',
    'questions': '/questions',
    'answers': '/answers',
    'submissions': '/submissions',
    'mcq-responses': '/mcq-responses',
    'essay-responses': '/essay-responses',
    // --- Class Assignments ---
    classAssignmentByTeacher: (teacherId) => `/assign/teacher/${teacherId}`,
    // --- Attachments ---
    chapterAttachments: (chapterId) => `/chapters/${chapterId}/attachments`,
    attachmentOpen: (id) => `/attachments/${id}/open`,
    attachmentDownload: (id) => `/attachments/${id}/download`,
    attachmentDelete: (id) => `/attachments/${id}`,
    // --- Exercises/Questions/Answer ---
    'questionsByExercise': (exerciseId) => `/questions/exercise/${exerciseId}`,
    'answersByQuestion': (questionId) => `/answers/question/${questionId}`,
    // --- Submissions ---
    'submissionsByExercise': (exerciseId) => `/submissions/exercise/${exerciseId}`,
    'submissionsByStudent': (studentId) => `/submissions/student/${studentId}`,
    'EssayResponsesByExercise': (exerciseId) => `/essay-responses/exercise/${exerciseId}`,
}

export const apiUrl = (path) =>
    `${BASE_URL.replace(/\/$/, '')}${path}`;


export const authApis = () => {
    const t = localStorage.getItem('token') || cookie.load('token'); // ưu tiên localStorage
    const headers = { 'Content-Type': 'application/json' };
    if (t) headers.Authorization = `Bearer ${t}`; // chỉ gắn khi thật sự có token
    return axios.create({ baseURL: BASE_URL, headers });
};

export default axios.create({
    baseURL: BASE_URL
});
