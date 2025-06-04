import { createApp } from 'vue'
import './style.css'
import 'vue-final-modal/style.css'
import App from './App.vue'
import router from "./router.ts";
import Popper from 'vue3-popper';
import "preline/preline";
import {createVfm} from "vue-final-modal";

createApp(App)
    .component('Popper', Popper)
    .use(router)
    .use(createVfm())
.mount('#app')
