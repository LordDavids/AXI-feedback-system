import axios from "axios";
import user from "./userStorage.ts";
import router from "./router.ts";

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    withCredentials: true,
});

api.interceptors.response.use((response) => {
    return response;
}, (error) => {
    if ((error.response.status === 401 || error.response.status === 403) && !error.response.config.url.includes('/auth/login') && !error.response.config.url.includes('/auth/logout')) {
        api.post("/auth/logout", {}, {
            headers: {
                'Access-Control-Allow-Origin': '*',
                'Content-Type': 'application/json'
            }
        }).then(() => {
            router.push({
                path: '/login',
                query: {logout: 'expired'},
            });
            user.value = null;
        }).catch((error) => {
            console.error(error);
        });
    }

    return Promise.reject(error);
});