import axios from "axios";
import cookie from "react-cookies";
const BASE_URL = 'http://localhost:8080/javaSpring/api/';

export const endpoints = {
    'users': '/users',
    'login': '/login',
    'auth': '/auth/user',
    'signup': '/users',
    'teachers': '/teachers',
    'students': '/students',
    'subjects': '/subjects',
    'classes': '/classes',
    'chapters': '/chapters',
    'excercises': '/exercises',
    'questions': '/questions',
    // --- Attachments ---
    chapterAttachments: (chapterId) => `/chapters/${chapterId}/attachments`,
    attachmentOpen: (id) => `/attachments/${id}/open`,
    attachmentDownload: (id) => `/attachments/${id}/download`,
    attachmentDelete: (id) => `/attachments/${id}`,
    // --- Exercises/Questions/Answer ---
    'questionsByExercise': (exerciseId) => `/questions/exercise/${exerciseId}`,
    'answersByQuestion': (questionId) => `/answers/question/${questionId}`,
}

export const apiUrl = (path) =>
    `${BASE_URL.replace(/\/$/, '')}${path}`;


export const authApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${cookie.load('token')}`,
            'Content-Type': 'application/json'
        }
    })
}
export default axios.create({
    baseURL: BASE_URL
});
