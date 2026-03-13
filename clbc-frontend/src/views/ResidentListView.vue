<script setup>
import { ref, onMounted, reactive } from 'vue';
import api from '../services/api';

const residents = ref([]);
const loading = ref(true);
const showAddModal = ref(false);
const submitting = ref(false);

const initialForm = {
  firstName: '',
  lastName: '',
  dateOfBirth: '',
  gender: 'MALE',
  email: '',
  phone: '',
  primaryPhysician: '',
  physicianContact: ''
};

const newResident = reactive({ ...initialForm });

const fetchResidents = async () => {
  try {
    const response = await api.get('/residents');
    residents.value = response.data.content;
  } catch (error) {
    console.error('Error fetching residents:', error);
  } finally {
    loading.value = false;
  }
};

const handleAddResident = async () => {
  submitting.value = true;
  try {
    await api.post('/residents', newResident);
    showAddModal.value = false;
    Object.assign(newResident, initialForm);
    await fetchResidents();
  } catch (error) {
    console.error('Error creating resident:', error);
    alert('Failed to add resident. Please check your connection or data.');
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  fetchResidents();
});
</script>

<template>
  <div class="page-header">
    <div>
      <h1>Residents</h1>
      <p>Manage community members and their care plans</p>
    </div>
    <button class="btn btn-primary" @click="showAddModal = true">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="M12 5v14"/></svg>
      Add Resident
    </button>
  </div>

  <!-- Add Resident Modal -->
  <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
    <div class="modal-content glass animate-fade-in">
      <div class="modal-header">
        <h2>Add New Resident</h2>
        <button class="close-btn" @click="showAddModal = false">&times;</button>
      </div>
      
      <form @submit.prevent="handleAddResident" class="resident-form">
        <div class="form-grid">
          <div class="form-group">
            <label>First Name</label>
            <input v-model="newResident.firstName" required type="text" placeholder="e.g. John">
          </div>
          <div class="form-group">
            <label>Last Name</label>
            <input v-model="newResident.lastName" required type="text" placeholder="e.g. Doe">
          </div>
          <div class="form-group">
            <label>Date of Birth</label>
            <input v-model="newResident.dateOfBirth" required type="date">
          </div>
          <div class="form-group">
            <label>Gender</label>
            <select v-model="newResident.gender">
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          </div>
          <div class="form-group">
            <label>Email</label>
            <input v-model="newResident.email" type="email" placeholder="john.doe@example.com">
          </div>
          <div class="form-group">
            <label>Phone</label>
            <input v-model="newResident.phone" type="tel" placeholder="(555) 000-0000">
          </div>
          <div class="form-group">
            <label>Primary Physician</label>
            <input v-model="newResident.primaryPhysician" type="text" placeholder="Dr. Smith">
          </div>
          <div class="form-group">
            <label>Physician Contact</label>
            <input v-model="newResident.physicianContact" type="text" placeholder="555-1234">
          </div>
        </div>
        
        <div class="form-actions">
          <button type="button" class="btn btn-secondary" @click="showAddModal = false">Cancel</button>
          <button type="submit" class="btn btn-primary" :disabled="submitting">
            <span v-if="submitting">Adding...</span>
            <span v-else>Save Resident</span>
          </button>
        </div>
      </form>
    </div>
  </div>

  <div v-if="loading" class="loading-state">
    <div class="loader"></div>
    <p>Loading residents...</p>
  </div>

  <div v-else class="resident-grid animate-fade-in">
    <div v-for="resident in residents" :key="resident.id" class="card resident-card">
      <div class="card-header">
        <div class="avatar-large">{{ resident.firstName[0] }}{{ resident.lastName[0] }}</div>
        <div class="status-badge" :class="resident.status.toLowerCase()">{{ resident.status }}</div>
      </div>
      <div class="card-content">
        <h3>{{ resident.firstName }} {{ resident.lastName }}</h3>
        <p class="resident-meta">{{ resident.gender }} • {{ resident.dateOfBirth }}</p>
        
        <div class="quick-stats">
          <div class="stat">
            <span class="stat-label">Room</span>
            <span class="stat-value">TBD</span>
          </div>
          <div class="stat">
            <span class="stat-label">Care Level</span>
            <span class="stat-value">Review Required</span>
          </div>
        </div>
      </div>
      <div class="card-footer">
        <router-link :to="{ name: 'ResidentDetail', params: { id: resident.id } }" class="btn btn-view">
          View Profile
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 4px;
}

.page-header p {
  color: var(--text-muted);
}

.resident-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.resident-card {
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, border-color 0.2s ease;
}

.resident-card:hover {
  transform: translateY(-4px);
  border-color: var(--primary);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.avatar-large {
  width: 56px;
  height: 56px;
  background: var(--primary-light);
  color: var(--primary);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
}

.status-badge {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  padding: 4px 10px;
  border-radius: 20px;
}

.status-badge.active {
  background: #ecfdf5;
  color: #10b981;
}

.status-badge.received {
  background: #fffbeb;
  color: #d97706;
}

.card-content h3 {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 4px;
}

.resident-meta {
  color: var(--text-muted);
  font-size: 14px;
  margin-bottom: 16px;
}

.quick-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  background: var(--bg-main);
  padding: 12px;
  border-radius: var(--radius-md);
  margin-bottom: 16px;
}

.stat {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 11px;
  color: var(--text-muted);
  text-transform: uppercase;
  font-weight: 600;
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
}

.card-footer {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.btn-view {
  width: 100%;
  color: var(--primary);
  font-weight: 600;
  font-size: 14px;
  gap: 6px;
}

.btn-view:hover {
  background: var(--primary-light);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  color: var(--text-muted);
}

.loader {
  width: 40px;
  height: 40px;
  border: 4px solid var(--border);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(4px);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.modal-content {
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  padding: 32px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: 700;
}

.close-btn {
  font-size: 28px;
  color: var(--text-muted);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

@media (max-width: 480px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
}

.form-group input, 
.form-group select {
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 14px;
  background: var(--bg-main);
  transition: all 0.2s ease;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--primary);
  background: white;
  box-shadow: 0 0 0 4px var(--primary-light);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--border);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
