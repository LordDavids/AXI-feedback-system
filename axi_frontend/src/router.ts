import {createRouter, createWebHistory} from "vue-router";
import HomeView from "./views/HomeView.vue";
import LoginView from "./views/auth/LoginView.vue";
import user from "./userStorage.ts";
import PageNotFoundView from "./views/PageNotFoundView.vue";
import PublicFormsOverview from './views/PublicFormsOverview.vue';
import TeamCreationView from "./views/TeamCreationView.vue";
import UnauthorizedView from "./views/UnauthorizedView.vue";
import SettingsView from "./views/SettingsView.vue";
import TeamDetailsView from "./views/TeamDetailsView.vue";
import RegisterView from "./views/auth/RegisterView.vue";
import { type IStaticMethods } from "preline/preline";
import UserSearchView from "./views/UserSearchView.vue";
import TeamEditView from "./views/TeamEditView.vue";
import FormsOverview from "./views/FormsOverview.vue";
import FormDetailsView from "./views/FormDetailsView.vue";
import FormEditView from "./views/FormEditView.vue";
import FormCreateView from "./views/FormCreateView.vue";
import UserFeedbackView from "./views/UserFeedbackView.vue";
import ReceivedFeedbackView from "./views/ReceivedFeedbackView.vue";


declare global {
    interface Window {
        HSStaticMethods: IStaticMethods;
    }
}

const routes = [
    {
        path: '/login',
        component: LoginView,
        meta: {
            hideNav: true,
            noAuth: true,
        },
    },
    {
        path: '/',
        component: HomeView,
        meta: {
            breadcrumbs: [
                { name: 'Teams', path: '/' }
            ]
        }
    },
    {
        path: '/team/:id',
        component: TeamDetailsView,
        meta: {
            breadcrumbs: [
                { name: 'Teams', path: '/' },
                { name: 'Team Details', path: '/team/:id' }
            ]
        }
    },
    {
        path: '/team/:id/edit',
        component: TeamEditView,
        meta: {
            breadcrumbs: [
                { name: 'Teams', path: '/' },
                { name: 'Team Details', path: '/team/:id' },
                { name: 'Team Aanpassen', path: '/team/:id/edit' }
            ]
        }
    },
    {
        path: '/team/:id/form',
        component: FormsOverview,
        meta: {
            breadcrumbs: [
                { name: 'Teams', path: '/' },
                { name: 'Team Details', path: '/team/:id' },
                { name: 'Vragenlijst', path: '/team/:id/form' }
            ]
        }
    },
    {
        path: '/team/:id/form/public-forms',
        name: 'PublicFormsOverview',
        component: PublicFormsOverview,
        meta: {
            breadcrumbs: [
                { name: 'Teams', path: '/' },
                { name: 'Team Details', path: '/team/:id' },
                { name: 'Vragenlijst', path: '/team/:id/form' },
                { name: 'Publieke Vragenlijst', path: '/team/:id/form/public-forms' }
            ]
        }
    },
    {
        path: '/team/:id/form/:fId',
        component: FormDetailsView,
        meta: {
            breadcrumbs: [
                { name: 'Teams', path: '/' },
                { name: 'Team Details', path: '/team/:id' },
                { name: 'Vragenlijst', path: '/team/:id/form' },
                { name: 'Vragenlijst Details', path: '/team/:id/form/:fId' }
            ]
        }
    },
    {
        path: '/team/:id/form/create',
        component: FormCreateView,
        meta: {
            breadcrumbs: [
                { name: 'Teams', path: '/' },
                { name: 'Team Details', path: '/team/:id' },
                { name: 'Vragenlijst', path: '/team/:id/form' },
                { name: 'Vragenlijst Aanmaken', path: '/team/:id/form/create' }
            ]
        }
    },
    {
        path: '/team/:id/form/:fId/edit',
        component: FormEditView,
        meta: {
            breadcrumbs: [
                { name: 'Teams', path: '/' },
                { name: 'Team Details', path: '/team/:id' },
                { name: 'Vragenlijst', path: '/team/:id/form' },
                { name: 'Vragenlijst Details', path: '/team/:id/form/:fId' },
                { name: 'Vragenlijst Aanpassen', path: '/team/:id/form/:fId/edit' }
            ]
        }
    },
    {
        path: '/team/:id/feedback/:uId?',
        component: ReceivedFeedbackView,
        meta: {
            breadcrumbs: [
                { name: 'Teams', path: '/' },
                { name: 'Team Details', path: '/team/:id' },
                { name: 'Ontvangen feedback', path: '/team/:id/:Uid' },
            ]
        }
    },
    {
        path: '/team/:id/feedback/to/:Uid',
        component: UserFeedbackView,
        meta: {
            breadcrumbs: [
                { name: 'Teams', path: '/' },
                { name: 'Team Details', path: '/team/:id' },
                { name: 'Gegeven Feedback', path: '/team/:id/:Uid' },
            ]
        }
    },
    {
        path: '/users',
        component: UserSearchView,
    },
    {
        path: '/:pathMatch(.*)*',
        component: PageNotFoundView,
    },
    {
        path: '/:pathMatch(.*)*',
        component: UnauthorizedView,
        name: 'unauthorized',
    },
    {
        path : '/team/create',
        component: TeamCreationView,
        meta: {
            requiredRole: ['admin', "manager"],
            breadcrumbs: [
                { name: 'Teams', path: '/' },
                { name: 'Team Aanmaken', path: '/team/create' },
            ]
        }
    },
    {
        path: '/register',
        component: RegisterView,
        meta: {
            requiredRole: ['admin'],
        }

    },
    {
        path: "/settings",
        component: SettingsView
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

router.beforeEach((to, from, next) => {
    if (to?.meta?.requiredRole && !user.value?.roles?.some((role) => to.meta.requiredRole.includes(role))) {
        next({
            name: 'unauthorized',
            replace: true,
            cause: new Error('Not authorized'),
            message: 'Not authorized',
        })
        return;
    }

    if (!to?.meta?.noAuth && !user.value) {
        next({
            path: '/login',
            replace: true,
            cause: new Error('Not logged in'),
            message: 'Not logged in',
        }) // Redirect to login page when not logged in
        return;
    }
    
    next();
});

router.afterEach((to, from, failure) => {
    if (!failure) {
        setTimeout(() => {
            window.HSStaticMethods.autoInit();
        }, 100)
    }
});

export default router;