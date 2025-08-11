
import { createContext } from "react";
import cookie from "react-cookies";
export const MyUserContext = createContext();
export const MyUserDispatchContext = createContext();
const MyUserReducer = (current, action) => {
    switch (action.type) {
        case "login":
            localStorage.setItem("token", action.payload.token);  // Lưu token
            return action.payload;

        case "logout":
            localStorage.removeItem("token"); // Xoá token khi logout
            cookie.remove("token", { path: "/" }); // Xoá cookie token
            return null;

        default:
            return current;
    }
};
export default MyUserReducer;