<script setup lang="ts">
import {onMounted, ref, watch} from 'vue';
import { api } from "../AxiosInstance.ts";
import {useModal} from "vue-final-modal";
import ModalConfirm, {MultiselectSubmitResult} from "../components/modals/MultiselectModal.vue";
import MultiselectModal from "../components/modals/MultiselectModal.vue";
import user from "../userStorage.ts";

const searchTerm = ref<string>('');
const users = ref<User[]>([]);
const roles = ref<Role[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);

let debounceTimeout: ReturnType<typeof setTimeout>;

const searchUsers = async () => {
  loading.value = true;
  error.value = null;

  await api.get<User[]>('/user/search', {
    params: {
      searchTerm: searchTerm.value,
    }
  })
      .then((response) => {
        users.value = response.data;
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};

const fetchRoles = async () => {
  await api.get<Role[]>('/roles')
      .then((response) => {
        roles.value = response.data
      })
}

const debouncedSearchUsers = () => {
  clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(() => {
    searchUsers();
  }, 200);
};

watch(searchTerm, debouncedSearchUsers);

onMounted(() => {
  searchUsers();
  fetchRoles();
});

const targetUser = ref<User | null>(null);

const handleUserRoleSubmit = ({target, selected}: MultiselectSubmitResult) => {
  api.put(`/user/${target.id}/roles`, 
    selected.map(role => role.id)
  )
  .then((response) => {
    close();
    
    users.value = users.value.map(user => {
      if (user.id === response.data.id){
        return response.data;
      }
      return user;
    });
  })
  .catch((err) => {
    error.value = err.message;
  });
}

const { open, close } = useModal({
  component: MultiselectModal,
  attrs: {
    title: `Bewerk rollen voor `,
    fieldName: 'Rollen',
    options: roles,
    target: targetUser,
    onSubmit: handleUserRoleSubmit
  },
  slots: {
    default: '',
  },
})

const openModal = (user: User) => {
  targetUser.value = user;
  open();
}

</script>

<template>
  <div>
    <div class="container mx-auto p-6 shadow-lg rounded-lg">
      <h1 class="text-2xl font-bold mb-4 text-left">Users</h1>
      <div v-if="error" class="text-red-500 mb-4">{{ error }}</div>
      <div class="w-full">
        <input
            v-model="searchTerm"
            type="text"
            name="searchTerm"
            class="w-full px-4 py-2 border rounded-lg text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-400"
            placeholder="Enter email..."
            required
        >
      </div>
      
      <div class="mt-4">
        <ul class="space-y-4">
          <li v-if="!loading && users.length === 0" class="text-gray-500">Geen gebruikers gevonden.</li>
          <li v-for="employee in users" :key="employee.id">
            <div class="flex justify-between items-center p-5 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer">
                <span>
                  {{ employee.firstName }} {{ employee.infix }} {{ employee.lastName }}
                </span>
              <div class="flex items-center space-x-4">
                <span v-if="employee.roles.includes('manager')" class="text-gray-500">
                  Manager
                </span>
                
                <button v-if="user.roles.includes('admin')" class="hover:bg-gray-200 active:bg-gray-100 p-1 rounded-lg" @click="openModal(employee)">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" class="size-6">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.325.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 0 1 1.37.49l1.296 2.247a1.125 1.125 0 0 1-.26 1.431l-1.003.827c-.293.241-.438.613-.43.992a7.723 7.723 0 0 1 0 .255c-.008.378.137.75.43.991l1.004.827c.424.35.534.955.26 1.43l-1.298 2.247a1.125 1.125 0 0 1-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.47 6.47 0 0 1-.22.128c-.331.183-.581.495-.644.869l-.213 1.281c-.09.543-.56.94-1.11.94h-2.594c-.55 0-1.019-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 0 1-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 0 1-1.369-.49l-1.297-2.247a1.125 1.125 0 0 1 .26-1.431l1.004-.827c.292-.24.437-.613.43-.991a6.932 6.932 0 0 1 0-.255c.007-.38-.138-.751-.43-.992l-1.004-.827a1.125 1.125 0 0 1-.26-1.43l1.297-2.247a1.125 1.125 0 0 1 1.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.086.22-.128.332-.183.582-.495.644-.869l.214-1.28Z" />
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                  </svg>
                </button>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>
