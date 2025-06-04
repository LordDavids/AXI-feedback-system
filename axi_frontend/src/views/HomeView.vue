<template>
  <div class="container px-0 sm:px-4">
    <div class="flex flex-col gap-2 p-2 rounded-lg shadow-lg bg-white">
      <div class="flex flex-col gap-2 p-4 pb-0">
        <div class="flex flex-row justify-between mb-2 items-baseline">
          <h1 class="font-bold text-4xl text-left">Teams</h1>
          <div v-if="user.roles.some(role => role === 'manager')">
            <router-link to="/team/create"
                         class="AXI-bg AXI-bg-hover text-white rounded-full w-9 h-9 flex items-center justify-center transition duration-300 ease-in-out">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                   stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15"/>
              </svg>
            </router-link>
          </div>
        </div>

        <input
            type="search"
            v-model="search"
            @change="onSearch"
            class="w-full border border-gray-300 p-2 pl-3 rounded-lg mb-4 bg-white placeholder-gray-500 focus:outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-200 transition duration-150 ease-in-out"
            placeholder="Zoeken..."
        />
      </div>

      <!-- Team List -->
      <div v-if="teams.length > 0" class="space-y-4 p-4">
        <div
            v-for="team in teams"
            :key="team.id"
            @click="handleTeamClick(team.id)"
            class="flex justify-between items-center p-3 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer"
        >
          <div class="team-info grid w-full text-left">
            <h2 class="text-lg font-semibold">{{ team.name }}</h2>
            <div class="text-sm text-gray-500 overflow-ellipsis whitespace-nowrap overflow-hidden w-3/4 inline-block">
              <template v-if="filteredManagers(team.users).value.length !== 0">
                <span v-for="(user, index) in filteredManagers(team.users).value" :key="user.id">
                  {{ user.firstName }} {{ user.infix ? user.infix : '' }} {{ user.lastName }}{{ index + 1 < filteredManagers(team.users).value.length ? ', ' : '' }}
                </span>
              </template>
              <template v-else>
                <span>
                  Geen manager
                </span>
              </template>
            </div>
          </div>
          <div class="min-w-max text-right">
            <span v-if="team.users.length > 1" class="text-sm text-gray-500">{{ team.users.length }} leden</span>
            <span v-else-if="team.users.length == 0" class="text-sm text-red-600">Geen leden</span>
            <span v-else class="text-sm text-gray-500">{{ team.users.length }} lid</span>
          </div>
        </div>
      </div>
    </div>

    <Pagination v-if="initDone && !error" class="mt-4" :total-pages="totalPages"
                :current-page="currentPage" @page-change="onPageChange"/>

    <!-- Loading or Error Handling -->
    <div v-if="!initDone" class="text-center text-gray-600">Laden...</div>
    <div v-if="error" class="text-center text-red-500">{{ error }}</div>
    <div v-if="!loading && teams.length === 0" class="text-center text-gray-600">Geen teams gevonden.</div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, computed} from 'vue';
import {api} from "../AxiosInstance.ts";
import user from "../userStorage.ts";
import Pagination from "../components/Pagination.vue";
import {useRoute, useRouter} from "vue-router";

const router = useRouter();
const route = useRoute();

const currentPage = ref(route.query.page ? parseInt(route.query.page as string) : 1);
const search = ref(route.query.page ? route.query.search as string : '');

const teams = ref<Team[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);
const totalPages = ref(0);
const initDone = ref(false);
const prevTeams = ref<Team[]>([]);


const handleTeamClick = (id: number) => {
  router.push(`/team/${id}`);
};

const fetchTeams = async () => {
  loading.value = true;
  error.value = null;

  const page = Math.max(0, currentPage.value - 1);

  await api.get<Team[]>(`/team?page=${page}&size=6&name=${search.value}`)
      .then((response) => {
        prevTeams.value = teams.value;  // Store current data
        teams.value = response.data.teams;
        totalPages.value = response.data.totalPages;

        if (currentPage.value > totalPages.value) {
          currentPage.value = totalPages.value;
          router.push({query: {page: totalPages.value, search: search.value}});
        } else if (currentPage.value < 1) {
          currentPage.value = 1;
          router.push({query: {page: 1, search: search.value}});
        } else {
          currentPage.value = page + 1;
          router.push({query: {page: page + 1, search: search.value}});
        }

        initDone.value = true;
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};

const onPageChange = ({page}: event) => {
  router.push({query: {page: page, search: search.value}});
  currentPage.value = page;
  fetchTeams();
};

const onSearch = () => {
  fetchTeams();
};

onMounted(fetchTeams);

const filteredManagers = (users: User[]) => {
  return computed(() => users.filter(user => user.roles.includes('manager')));
};
</script>

<style scoped>
</style>
