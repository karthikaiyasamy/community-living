import axios from 'axios';
import { useAuthStore } from '../store/auth';
import { useNotificationStore } from '../store/notification';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

api.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    const tenantId = localStorage.getItem('tenantId');

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    if (tenantId) {
        config.headers['X-Tenant-ID'] = tenantId;
    }

    return config;
}, error => {
    return Promise.reject(error);
});

api.interceptors.response.use(response => {
    return response;
}, error => {
    if (error.response && error.response.status === 401) {
        const authStore = useAuthStore();
        const notificationStore = useNotificationStore();

        // Check if we were already on login to avoid loop
        if (window.location.pathname !== '/login') {
            authStore.logout('/login?reason=expired');
        }
    }
    return Promise.reject(error);
});

export default api;
