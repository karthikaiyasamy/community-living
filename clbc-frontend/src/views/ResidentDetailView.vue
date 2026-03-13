<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import api from '../services/api';
import { useNotificationStore } from '../store/notification';
import ObservationChart from '../components/ObservationChart.vue';

const notificationStore = useNotificationStore();

const route = useRoute();
const residentId = route.params.id;

const resident = ref(null);
const preferences = ref(null);
const prescriptions = ref([]);
const observations = ref([]);
const documents = ref([]);
const administrations = ref({}); // Map of prescriptionId -> list of administrations
const vitalHistory = ref({});
const activeTab = ref('overview');
const loading = ref(true);
const uploading = ref(false);
const recordingAdmin = ref(false);
const savingPreferences = ref(false);
// Local toast refs removed in favor of global store

const vitalTypes = [
  { type: 'Blood Pressure', label: 'Blood Pressure', color: '#ef4444' }, // red
  { type: 'Heart Rate', label: 'Heart Rate', color: '#ec4899' }, // pink
  { type: 'Glucose', label: 'Blood Glucose', color: '#f59e0b' }, // amber
  { type: 'Weight', label: 'Body Weight', color: '#10b981' } // emerald
];

const fetchData = async () => {
  try {
    const [resResp, prefResp, prescResp, obsResp, docsResp] = await Promise.all([
      api.get(`/residents/${residentId}`),
      api.get(`/residents/${residentId}/preferences`),
      api.get(`/residents/${residentId}/prescriptions`),
      api.get(`/residents/${residentId}/observations`),
      api.get(`/residents/${residentId}/documents`)
    ]);
    
    resident.value = resResp.data;
    preferences.value = prefResp.data;
    prescriptions.value = prescResp.data;
    observations.value = obsResp.data;
    documents.value = docsResp.data;

    // Fetch administrations for each active prescription
    const adminPromises = prescriptions.value
      .filter(p => p.status === 'ACTIVE')
      .map(p => 
        api.get(`/prescriptions/${p.id}/administrations`)
          .then(resp => ({ id: p.id, data: resp.data }))
      );
    
    const adminResults = await Promise.all(adminPromises);
    const adminMap = {};
    adminResults.forEach(res => {
      adminMap[res.id] = res.data;
    });
    administrations.value = adminMap;

    // Fetch history for each vital type for charts
    const historyPromises = vitalTypes.map(v => 
      api.get(`/residents/${residentId}/observations`, { params: { type: v.type } })
        .then(resp => ({ type: v.type, data: resp.data }))
    );

    const histories = await Promise.all(historyPromises);
    const historyMap = {};
    histories.forEach(h => {
      historyMap[h.type] = h.data;
    });
    vitalHistory.value = historyMap;

  } catch (error) {
    console.error('Error fetching resident data:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchData();
});

const recordDose = async (prescriptionId, taken) => {
  recordingAdmin.value = true;
  try {
    const now = new Date();
    const payload = {
      dateAdministered: now.toISOString().split('T')[0],
      timeAdministered: now.toTimeString().split(' ')[0],
      doseTaken: taken,
      notes: taken ? 'Dose administered as prescribed' : 'Dose missed/refused'
    };
    
    const response = await api.post(`/prescriptions/${prescriptionId}/administrations`, payload);
    
    if (!administrations.value[prescriptionId]) {
      administrations.value[prescriptionId] = [];
    }
    administrations.value[prescriptionId].unshift(response.data);
    notificationStore.success('Medication dose recorded successfully');
  } catch (error) {
    console.error('Failed to record dose:', error);
    notificationStore.error('Failed to record administration');
  } finally {
    recordingAdmin.value = false;
  }
};

const isDoseTakenToday = (prescriptionId) => {
  const admins = administrations.value[prescriptionId];
  if (!admins || admins.length === 0) return false;
  
  const today = new Date().toISOString().split('T')[0];
  return admins.some(a => a.dateAdministered === today && a.doseTaken);
};

const handleFileUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append('file', file);

  uploading.value = true;
  try {
    const response = await api.post(`/residents/${residentId}/documents/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    documents.value.unshift(response.data);
    notificationStore.success('Document uploaded successfully');
  } catch (error) {
    console.error('Upload failed:', error);
    notificationStore.error('Failed to upload document');
  } finally {
    uploading.value = false;
    event.target.value = ''; 
  }
};

const downloadDocument = async (doc) => {
  try {
    const response = await api.get(`/residents/${residentId}/documents/${doc.id}/download`, {
      responseType: 'blob'
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', doc.documentName);
    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch (error) {
    console.error('Download failed:', error);
  }
};

const deleteDocument = async (id) => {
  if (!confirm('Are you sure you want to delete this document?')) return;
  try {
    await api.delete(`/residents/${residentId}/documents/${id}`);
    documents.value = documents.value.filter(d => d.id !== id);
    notificationStore.success('Document deleted');
  } catch (error) {
    console.error('Delete failed:', error);
    notificationStore.error('Failed to delete document');
  }
};

const formatSize = (bytes) => {
  if (!bytes) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
};

const savePreferences = async () => {
  savingPreferences.value = true;
  try {
    const response = await api.put(`/residents/${residentId}/preferences`, preferences.value);
    preferences.value = response.data;
    notificationStore.success('Resident preferences updated successfully');
  } catch (error) {
    console.error('Error saving preferences:', error);
    notificationStore.error('Failed to save preferences. Please try again.');
  } finally {
    savingPreferences.value = false;
  }
};
</script>

<template>
  <div v-if="loading" class="loading-state">
    <div class="loader"></div>
    <p>Loading clinical records...</p>
  </div>

  <div v-else-if="resident" class="detail-container animate-fade-in">
    <!-- Global toast handled in App.vue -->

    <div class="profile-header card">
       <div class="header-left">
          <div class="avatar-huge">{{ resident.firstName[0] }}{{ resident.lastName[0] }}</div>
          <div class="profile-title">
            <h1>{{ resident.firstName }} {{ resident.lastName }}</h1>
            <p>{{ resident.status }} • Resident ID: {{ resident.id }}</p>
          </div>
       </div>
       <div class="header-right">
         <button class="btn btn-primary">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
            Edit Profile
         </button>
       </div>
    </div>

    <div class="tabs-nav border-bottom">
      <button v-for="tab in ['Overview', 'MAR', 'Medications', 'Observations', 'Documents', 'Preferences']" 
              :key="tab"
              @click="activeTab = tab.toLowerCase()"
              class="tab-link"
              :class="{ active: activeTab === tab.toLowerCase() }">
        {{ tab }}
      </button>
    </div>

    <div class="tab-content">
      <!-- Overview Tab -->
      <div v-if="activeTab === 'overview'" class="tab-pane">
        <div class="info-grid">
           <div class="card">
              <h3>Personal Information</h3>
              <div class="info-row">
                <span class="label">Date of Birth</span>
                <span class="value">{{ resident.dateOfBirth }}</span>
              </div>
              <div class="info-row">
                <span class="label">Gender</span>
                <span class="value">{{ resident.gender }}</span>
              </div>
              <div class="info-row">
                <span class="label">Primary Physician</span>
                <span class="value">{{ resident.primaryPhysician || 'N/A' }}</span>
              </div>
              <div class="info-row">
                <span class="label">Physician Contact</span>
                <span class="value">{{ resident.physicianContact || 'N/A' }}</span>
              </div>
           </div>
           
           <div class="card">
              <h3>Recent Vitals</h3>
              <div class="vitals-grid">
                 <div v-for="obs in observations.slice(0, 4)" :key="obs.id" class="vital-card">
                    <span class="vital-label">{{ obs.type }}</span>
                    <span class="vital-value">{{ obs.value }} <small>{{ obs.unit }}</small></span>
                 </div>
              </div>
           </div>
        </div>
      </div>

      <!-- MAR Tab -->
      <div v-if="activeTab === 'mar'" class="tab-pane">
         <div class="mar-container">
            <div class="card mar-header-card">
               <div class="mar-title-group">
                  <h3>Medication Administration Record</h3>
                  <p class="text-xs text-muted">Sign off doses for today: {{ new Date().toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}</p>
               </div>
            </div>

            <div class="mar-grid">
               <div v-for="presc in prescriptions.filter(p => p.status === 'ACTIVE')" :key="presc.id" class="card mar-item-card">
                  <div class="mar-item-header">
                     <div class="mar-med-info">
                        <span class="med-name">{{ presc.medicationName }}</span>
                        <span class="med-dose">{{ presc.dosage }} • {{ presc.frequency }}</span>
                     </div>
                     <div class="mar-status-badge">
                        <span v-if="isDoseTakenToday(presc.id)" class="badge badge-success">COMPLETED</span>
                        <span v-else class="badge badge-pending">PENDING</span>
                     </div>
                  </div>

                  <div class="mar-actions">
                     <button 
                        @click="recordDose(presc.id, true)" 
                        class="btn btn-primary btn-sm"
                        :disabled="isDoseTakenToday(presc.id) || recordingAdmin">
                        Mark as Taken
                     </button>
                     <button 
                        @click="recordDose(presc.id, false)" 
                        class="btn btn-outline btn-sm"
                        :disabled="isDoseTakenToday(presc.id) || recordingAdmin">
                        Skip / Refused
                     </button>
                  </div>

                  <div v-if="administrations[presc.id]?.length" class="mar-history">
                     <span class="history-label">Recent Logs</span>
                     <div class="history-scroll">
                        <div v-for="admin in administrations[presc.id].slice(0, 3)" :key="admin.id" class="history-item">
                           <span class="item-time">{{ admin.timeAdministered.slice(0, 5) }}</span>
                           <span class="item-status" :class="{ 'text-success': admin.doseTaken, 'text-danger': !admin.doseTaken }">
                              {{ admin.doseTaken ? 'Taken' : 'Missed' }}
                           </span>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>

      <!-- Medications Tab -->
      <div v-if="activeTab === 'medications'" class="tab-pane">
         <div class="card">
            <div class="pane-header">
               <h3>Active Prescriptions</h3>
               <button class="btn btn-outline btn-sm">+ Add New</button>
            </div>
            <table class="data-table">
               <thead>
                  <tr>
                     <th>Medication</th>
                     <th>Dosage</th>
                     <th>Frequency</th>
                     <th>Route</th>
                     <th>Status</th>
                  </tr>
               </thead>
               <tbody>
                  <tr v-for="presc in prescriptions" :key="presc.id">
                     <td class="font-bold">{{ presc.medicationName }}</td>
                     <td>{{ presc.dosage }}</td>
                     <td>{{ presc.frequency }}</td>
                     <td>{{ presc.route }}</td>
                     <td><span class="badge" :class="'badge-' + presc.status.toLowerCase()">{{ presc.status }}</span></td>
                  </tr>
               </tbody>
            </table>
         </div>
      </div>

      <!-- Observations Tab -->
      <div v-if="activeTab === 'observations'" class="tab-pane">
         <div class="clinical-dashboard">
            <div class="charts-section">
               <div v-for="v in vitalTypes" :key="v.type" class="card chart-card">
                  <div class="chart-header">
                     <h3>{{ v.label }} Trend</h3>
                     <span class="badge badge-system" v-if="vitalHistory[v.type]?.length">Live Data</span>
                  </div>
                  <div v-if="vitalHistory[v.type]?.length > 1" class="chart-wrapper">
                     <ObservationChart 
                        :label="v.label" 
                        :observations="vitalHistory[v.type]" 
                        :color="v.color" />
                  </div>
                  <div v-else class="empty-chart">
                     <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="m19 9-5 5-4-4-3 3"/></svg>
                     <p>Insufficient data points for chart</p>
                  </div>
               </div>
            </div>

            <div class="card recent-logs">
               <h3>Clinical Log</h3>
               <div class="vitals-list">
                  <div v-for="obs in observations" :key="obs.id" class="vital-row">
                     <div class="row-left">
                        <span class="vital-type">{{ obs.type }}</span>
                        <span class="vital-time">{{ new Date(obs.observedAt).toLocaleString() }}</span>
                     </div>
                     <span class="vital-data">{{ obs.value }} <small>{{ obs.unit }}</small></span>
                  </div>
                  <div v-if="observations.length === 0" class="empty-state">No observations recorded</div>
               </div>
            </div>
         </div>
      </div>

      <!-- Documents Tab -->
      <div v-if="activeTab === 'documents'" class="tab-pane">
         <div class="card">
            <div class="pane-header">
               <div>
                  <h3>Medical Documents</h3>
                  <p class="text-xs text-muted">Upload and manage specialist reports, ID cards, and consent forms</p>
               </div>
               <div class="upload-actions">
                  <input type="file" id="docUpload" @change="handleFileUpload" hidden />
                  <label for="docUpload" class="btn btn-primary btn-sm" :class="{ disabled: uploading }">
                     {{ uploading ? 'Uploading...' : '+ Upload Document' }}
                  </label>
               </div>
            </div>

            <div v-if="documents.length === 0" class="empty-documents">
               <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="text-muted"><path d="M4 22h14a2 2 0 0 0 2-2V7.5L14.5 2H6a2 2 0 0 0-2 2v4"/><path d="M14 2v6h6"/><path d="M3 15h9"/><path d="m9 18 3-3-3-3"/></svg>
               <p>No documents uploaded yet</p>
            </div>

            <div v-else class="document-list">
               <div v-for="doc in documents" :key="doc.id" class="document-item card">
                  <div class="doc-icon">
                     <svg v-if="doc.contentType?.includes('pdf')" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/><path d="M9 13v-3"/><path d="M12 13v-3"/><path d="M15 13v-3"/></svg>
                     <svg v-else xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#6366f1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>
                  </div>
                  <div class="doc-info">
                     <span class="doc-name">{{ doc.documentName }}</span>
                     <span class="doc-meta">{{ formatSize(doc.fileSize) }} • {{ new Date(doc.uploadedAt).toLocaleDateString() }}</span>
                  </div>
                  <div class="doc-actions">
                     <button @click="downloadDocument(doc)" class="btn-icon" title="Download">
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
                     </button>
                     <button @click="deleteDocument(doc.id)" class="btn-icon text-danger" title="Delete">
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/><line x1="10" y1="11" x2="10" y2="17"/><line x1="14" y1="11" x2="14" y2="17"/></svg>
                     </button>
                  </div>
               </div>
            </div>
         </div>
      </div>

      <!-- Preferences Tab -->
      <div v-if="activeTab === 'preferences'" class="tab-pane">
         <div class="card">
            <h3>Resident Preferences</h3>
            <div class="form-grid">
              <div class="form-group">
                <label>Dietary Preferences</label>
                <textarea v-model="preferences.dietaryPreferences" rows="3"></textarea>
              </div>
              <div class="form-group">
                <label>Activity Preferences</label>
                <textarea v-model="preferences.activityPreferences" rows="3"></textarea>
              </div>
              <div class="form-group">
                <label>Communication Preferences</label>
                <textarea v-model="preferences.communicationPreferences" rows="3"></textarea>
              </div>
              <div class="form-group">
                <label>Care Notes</label>
                <textarea v-model="preferences.careNotes" rows="3"></textarea>
              </div>
            </div>
            <div class="form-actions">
              <button @click="savePreferences" class="btn btn-primary" :disabled="savingPreferences">
                <span v-if="savingPreferences" class="spinner-sm"></span>
                {{ savingPreferences ? 'Saving...' : 'Save Changes' }}
              </button>
            </div>
         </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.toast-notification {
  position: fixed;
  bottom: 32px;
  right: 32px;
  z-index: 1000;
  padding: 12px 24px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 14px;
}

.toast-notification.success {
  border-left: 4px solid #10b981;
  color: #064e3b;
}

.toast-notification.error {
  border-left: 4px solid #ef4444;
  color: #7f1d1d;
}

.toast-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.spinner-sm {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
  display: inline-block;
  margin-right: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.3s ease-out;
}
.slide-up-enter-from {
  transform: translateY(20px);
  opacity: 0;
}
.slide-up-leave-to {
  transform: translateY(20px);
  opacity: 0;
}

.avatar-huge {
  width: 80px;
  height: 80px;
  background: var(--primary-light);
  color: var(--primary);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.profile-title h1 {
  font-size: 24px;
  margin-bottom: 4px;
}

.profile-title p {
  color: var(--text-muted);
}

.tabs-nav {
  display: flex;
  gap: 32px;
  margin-bottom: 24px;
}

.tab-link {
  padding: 12px 4px;
  font-weight: 600;
  color: var(--text-muted);
  background: transparent;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  font-size: 15px;
  transition: all 0.2s ease;
}

.tab-link:hover {
  color: var(--primary);
}

.tab-link.active {
  color: var(--primary);
  border-bottom-color: var(--primary);
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
}

.info-row:last-child {
  border-bottom: none;
}

.label {
  color: var(--text-muted);
  font-size: 14px;
}

.value {
  font-weight: 600;
}

.pane-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  text-align: left;
  padding: 12px;
  border-bottom: 2px solid var(--border);
  color: var(--text-muted);
  font-size: 13px;
  text-transform: uppercase;
}

.data-table td {
  padding: 16px 12px;
  border-bottom: 1px solid var(--border);
}

.vitals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
}

.vital-card {
  background: var(--bg-main);
  padding: 16px;
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
}

.vital-label {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.vital-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-main);
}

.vital-value small {
  font-size: 12px;
  font-weight: 400;
  color: var(--text-muted);
}

.clinical-dashboard {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 24px;
  align-items: start;
}

.charts-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.chart-card {
  padding: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 600;
}

.empty-chart {
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  background: var(--bg-main);
  border-radius: var(--radius-md);
  gap: 12px;
}

.empty-chart p {
  font-size: 13px;
}

.recent-logs h3 {
  margin-bottom: 20px;
}

.vitals-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.vital-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: var(--bg-main);
  border-radius: var(--radius-md);
}

.row-left {
  display: flex;
  flex-direction: column;
}

.vital-type {
  font-weight: 600;
  font-size: 14px;
}

.vital-time {
  font-size: 11px;
  color: var(--text-muted);
}

.vital-data {
  font-weight: 700;
  color: var(--primary);
}

.badge-system {
  background: #dcfce7;
  color: #166534;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 99px;
}

/* MAR Styles */
.mar-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.mar-header-card {
  padding: 20px;
}

.mar-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.mar-item-card {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.mar-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.mar-med-info {
  display: flex;
  flex-direction: column;
}

.med-name {
  font-weight: 700;
  font-size: 18px;
  color: var(--text-main);
}

.med-dose {
  font-size: 13px;
  color: var(--text-muted);
}

.mar-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.mar-history {
  border-top: 1px solid var(--border);
  padding-top: 12px;
}

.history-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  margin-bottom: 8px;
  display: block;
}

.history-scroll {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  background: var(--bg-main);
  padding: 6px 10px;
  border-radius: 4px;
}

.badge-success {
  background: #dcfce7;
  color: #166534;
}

.badge-pending {
  background: #fef9c3;
  color: #854d0e;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
}

.empty-documents {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: var(--text-muted);
  gap: 16px;
}

.document-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.document-item {
  display: flex;
  align-items: center;
  padding: 16px;
  gap: 12px;
}

.doc-info {
  flex: 1;
}

.doc-name {
  font-weight: 600;
  font-size: 14px;
}

.doc-meta {
  font-size: 11px;
  color: var(--text-muted);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
}

.form-group textarea {
  padding: 12px;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  font-family: inherit;
}

.form-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 1200px) {
  .clinical-dashboard {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .charts-section {
    grid-template-columns: 1fr;
  }
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
