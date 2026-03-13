import { defineStore } from 'pinia';

export const useNotificationStore = defineStore('notification', {
    state: () => ({
        message: null,
        type: 'success', // 'success', 'error', 'warning', 'info'
        show: false,
        timeout: 5000
    }),

    actions: {
        notify(message, type = 'success', duration = 5000) {
            this.message = message;
            this.type = type;
            this.show = true;
            this.timeout = duration;

            setTimeout(() => {
                this.clear();
            }, duration);
        },

        success(message, duration) {
            this.notify(message, 'success', duration);
        },

        error(message, duration) {
            this.notify(message, 'error', duration);
        },

        warning(message, duration) {
            this.notify(message, 'warning', duration);
        },

        clear() {
            this.show = false;
            // Delay clearing message to allow transition to finish
            setTimeout(() => {
                if (!this.show) {
                    this.message = null;
                }
            }, 300);
        }
    }
});
