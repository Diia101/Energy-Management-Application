import React, { useEffect, useRef, useState } from 'react';
import { Bar } from 'react-chartjs-2';
import axios from 'axios';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';

// Înregistrează scalele și alte componente necesare
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const MonitoringChart = ({ selectedDeviceId }) => {
    const [chartData, setChartData] = useState([]);
    const [labels, setLabels] = useState([]);
    const chartRef = useRef(null);
    const [errorMessage, setErrorMessage] = useState('');

    const fetchData = async (deviceId) => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                console.log('Token not found in local storage');
                setErrorMessage('Token not found. Please log in again.');
                return;
            }

            const response = await axios.get(`http://measurements-service.localhost/measurement/getSum?idDevice=${deviceId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            console.log('Response data:', response.data); // Log pentru debugging

            if (typeof response.data === 'number') {
                setChartData([response.data]);
                setLabels(['Data']);
            } else if (Array.isArray(response.data) && response.data.length > 0) {
                setChartData(response.data);
                setLabels(response.data.map((_, index) => `Label ${index + 1}`));
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
        if (selectedDeviceId !== null) {
            fetchData(selectedDeviceId);
        }
    }, [selectedDeviceId]);

    // Chart.js data structure
    const data = {
        labels: labels,
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

    // Chart.js options
    const options = {
        responsive: true,
        maintainAspectRatio: false,
    };

    // Ensure the previous chart is destroyed before re-rendering
    useEffect(() => {
        if (chartRef.current && chartRef.current.chartInstance) {
            chartRef.current.chartInstance.destroy();
        }
    }, [chartData]);

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
