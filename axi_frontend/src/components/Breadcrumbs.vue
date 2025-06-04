<template>
  <nav class="breadcrumbs">
    <ul class="!hidden md:!flex">
      <li v-for="(breadcrumb, index) in breadcrumbs" :key="index" class="breadcrumb-item">
        <button v-if="index < breadcrumbs.length - 1" @click="navigateTo(breadcrumb.path)">
          {{ breadcrumb.name }}
        </button>
        <span v-else>{{ breadcrumb.name }}</span>
        <span v-if="index < breadcrumbs.length - 1" class="breadcrumb-separator"> / </span>
      </li>
    </ul>

    <div class="md:hidden">
      <button class="pr-2" v-if="breadcrumbs.length >= 2" @click="navigateTo(breadcrumbs[breadcrumbs.length - 2].path)">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
          <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5 3 12m0 0 7.5-7.5M3 12h18" />
        </svg>
      </button>
      <span class="align-top">{{ breadcrumbs[breadcrumbs.length - 1].name }}</span>
    </div>
  </nav>
</template>

<script>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

export default {
  setup() {
    const route = useRoute();
    const router = useRouter();

    const breadcrumbs = computed(() => {
      return route.meta.breadcrumbs.map(breadcrumb => {
        const path = breadcrumb.path.replace(/:([a-zA-Z]+)/g, (_, key) => route.params[key]);
        return { ...breadcrumb, path };
      });
    });

    const navigateTo = (path) => {
      router.push(path);
    };

    return {
      breadcrumbs,
      navigateTo
    };
  }
};
</script>

<style scoped>
.breadcrumbs {
  display: flex;
  align-items: center;
  flex-wrap: wrap; /* Allow wrapping of breadcrumb items */
}

.breadcrumbs ul {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
  flex-wrap: wrap; /* Allow the list items to wrap */
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  margin-right: 5px; /* Space between breadcrumb items */
}

.breadcrumb-separator {
  margin: 0 5px; /* Adjust spacing between breadcrumb items */
  color: #888; /* Optional: Style separator color */
}

button {
  background: none;
  border: none;
  color: #7A36FF;
  cursor: pointer;
  font-weight: bold;
}

button:focus, button:hover {
  color: #5427b1; /* Optional: Change color on hover/focus */
  text-decoration: underline;
}

span {
  font-weight: bold;
}
</style>
