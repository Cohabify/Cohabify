import { createRouter, createWebHistory } from 'vue-router';
import Main from './components/Main.vue';
import Login from './components/authentication/Login.vue';
import Register from './components/authentication/Register.vue';
import UserAdvertisementDetails from './components/userAdvertisement/UserAdvertisementDetails.vue';
import HouseAdvertisementDetails from './components/houseAdvertisement/HouseAdvertisementDetails.vue';

const routes = [
    { path: '/', component: Main },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    {path: '/userAdvertisement/1', component: UserAdvertisementDetails },
    {path: '/advertisements/house/:id', component: HouseAdvertisementDetails }
];
  
const router = createRouter({
    routes,
    history: createWebHistory()
});
  
export default router;