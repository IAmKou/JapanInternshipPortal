import { Calendar } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';

// Correctly import the FullCalendar CSS
import '@fullcalendar/core/main.css';
import '@fullcalendar/daygrid/main.css';


document.addEventListener('DOMContentLoaded', function () {
    var calendarEl = document.getElementById('calendar');
    var calendar = new Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        plugins: [dayGridPlugin, interactionPlugin],
        events: [],
        editable: true,
        droppable: true, // Allow events to be dragged and dropped
    });
    calendar.render();
});
