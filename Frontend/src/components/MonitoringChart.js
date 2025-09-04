import React, { useEffect, useRef, useState } from 'react';
import { Bar } from 'react-chartjs-2';
import axios from 'axios';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import {HOST_MEASUREMENT} from "../Hosts";

// Înregistrează scalele și alte componente necesare
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const MonitoringChart = ({ selectedDeviceId, selectedDate }) => {
    const [chartData, setChartData] = useState([]); //valorile de consum pt fiecare ora
    const [labels, setLabels] = useState([]); //stochez orele zilei care vor fi afisate pe axa x a graficului
    const chartRef = useRef(null); //referinta catre instanta graficului pt a asigura distrugerea acestuia inainte de randare
    const [errorMessage, setErrorMessage] = useState(''); //mesdaj de eroare

    //preiau datele zilnice pt dispozitive si data specificata
    const fetchData = async (deviceId, date) => {
        try {
            //iau token din localstore
            const token = localStorage.getItem('token');
            if (!token) {
                console.log('Token not found in local storage');
                setErrorMessage('Token not found. Please log in again.');
                return;
            }

            const formattedDate = date.toISOString().split('T')[0]; // formatez data pentru a fi trimisă (YYYY-MM-DD)
            console.log("Formated date: ", formattedDate);
            console.log("Id ul pt device ce face (poate) figuri: ", deviceId);
            //const response = await axios.get(`http://measurements-service.localhost/measurement/getSum?idDevice=${deviceId}&date=${formattedDate}`, {
            const response = await axios.get(`http://measurements-service.localhost/measurement/getDailyData?idDevice=${deviceId}&date=${formattedDate}`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    // 'Accept': 'application/json',
                    // 'Content-Type': 'application/json; charset=UTF-8',
                    // 'Authorization': 'Bearer ' + token
                },
            });

            console.log('Response data:', response.data);


            if (typeof response.data === 'object' && response.data !== null) {
                // Prelucrarea datelor de tip Map<String, Double> (timpul pe ore și valorile)
                const hours = Object.keys(response.data);  // Obține toate orele (cheile)
                const values = Object.values(response.data);  // Obține valorile asociate fiecărei ore

                setChartData(values);
                setLabels(hours);  // folosesc orele ca etichete pe axa X

            } else {
                setChartData([]);
                setLabels([]);
                setErrorMessage('No data available for this device.');
            }

        } catch (error) {
            console.error('Error fetching data', error);
            setErrorMessage('Error fetching data. Please try again later.');
        }
    };

    useEffect(() => {
        if (selectedDeviceId !== null && selectedDate) {
            fetchData(selectedDeviceId, selectedDate);  // apelez functia de fiecare data cand deviceid si data se schimba
        }
    }, [selectedDeviceId, selectedDate]);  // depinde de dispozitivul și data selectată

    //chart.js; structura graficului
    const data = {
        labels: labels, //ora pe axa x
        datasets: [
            {
                label: 'kWh',
                data: chartData,
                backgroundColor: 'rgba(128, 0, 128, 0.5)',
                borderColor: 'rgba(0, 0, 0, 1)',
                borderWidth: 1,
            },
        ],
    };

    // chart.js ; optiuni pentru grafic
    const options = {
        responsive: true, //graficul se ajusteaza automat la dimensiunea containerului
        maintainAspectRatio: false, //dezactivez pastrarea raportului aspectului
    };

    // ma asigur că graficul anterior este distrus înainte de re-randare
    useEffect(() => {
        if (chartRef.current && chartRef.current.chartInstance) {
            chartRef.current.chartInstance.destroy();
        }
    }, [chartData]);

    //returnarea componentei
    return (
        <div style={{ width: '600px', height: '400px' }}>
            {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
            {chartData.length > 0 ? (
                <Bar data={data} options={options} ref={chartRef} />
            ) : (
                <p>No data available to display.</p>
            )}
        </div>
    );
};

export default MonitoringChart;
