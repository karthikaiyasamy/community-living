import { defineStore } from 'pinia';
import api from '../services/api';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: JSON.parse(localStorage.getItem('user')) || null,
        token: localStorage.getItem('token') || null,
        loading: false,
        error: null
    }),

    getters: {
        isAuthenticated: (state) => !!state.token,
        role: (state) => state.user?.role
    },

    actions: {
        async login(email, password) {
            this.loading = true;
            this.error = null;
            try {
                const response = await api.post('/auth/login', { email, password });
                const { accessToken, user } = response.data;

                this.token = accessToken;
                this.user = user;

                localStorage.setItem('token', accessToken);
                localStorage.setItem('user', JSON.stringify(user));
                localStorage.setItem('tenantId', user.communityId);

                return true;
            } catch (error) {
                this.error = error.response?.data?.message || 'Login failed';
                return false;
            } finally {
                this.loading = false;
            }
        },

        logout(redirectUrl = '/login') {
            this.token = null;
            this.user = null;
            localStorage.clear();
            window.location.href = redirectUrl;
        }
    }
});
