import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';

const ChatWidget = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([
        { sender: 'bot', text: 'Hello! I am your AI assistant. How can I help you today?' }
    ]);
    const [input, setInput] = useState('');
    const messagesEndRef = useRef(null);

    const toggleChat = () => setIsOpen(!isOpen);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages, isOpen]);

    const sendMessage = async (e) => {
        e.preventDefault();
        if (!input.trim()) return;

        const userMessage = { sender: 'user', text: input };
        setMessages((prev) => [...prev, userMessage]);
        setInput('');

        try {
            const baseUrl = import.meta.env.VITE_API_URL || '';
            const response = await axios.post(`${baseUrl}/api/chatbot/ask`, {
                query: input
            });
            const botMessage = { sender: 'bot', text: response.data.response };
            setMessages((prev) => [...prev, botMessage]);
        } catch (error) {
            setMessages((prev) => [...prev, { sender: 'bot', text: 'Error connecting to the server.' }]);
        }
    };

    return (
        <div className="fixed bottom-6 right-6 z-50">
            {isOpen ? (
                <div className="bg-white rounded-lg shadow-2xl w-80 h-96 flex flex-col border border-gray-200 overflow-hidden transform transition-all">
                    <div className="bg-red-600 text-white p-4 font-bold flex justify-between items-center shadow-md">
                        <span className="flex items-center gap-2">
                           <svg xmlns="http://www.w3.org/.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                <path fillRule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7zM7 9H5v2h2V9zm8 0h-2v2h2V9zM9 9h2v2H9V9z" clipRule="evenodd" />
                            </svg>
                            BMS Assistant
                        </span>
                        <button onClick={toggleChat} className="text-white hover:text-gray-200 transition-colors">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>
                    <div className="flex-1 p-4 overflow-y-auto bg-gray-50 flex flex-col gap-3">
                        {messages.map((msg, idx) => (
                            <div key={idx} className={`p-3 rounded-xl max-w-[80%] text-sm shadow-sm ${msg.sender === 'user' ? 'bg-red-500 text-white self-end rounded-br-none' : 'bg-white text-gray-800 self-start border border-gray-200 rounded-bl-none'}`}>
                                {msg.text}
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>
                    <form onSubmit={sendMessage} className="p-3 bg-white border-t flex gap-2">
                        <input
                            type="text"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            placeholder="Type your message..."
                            className="flex-1 border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-red-500 bg-gray-50"
                        />
                        <button type="submit" className="bg-red-600 text-white px-3 py-2 rounded-md hover:bg-red-700 transition">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M6 12L3.269 3.126A59.768 59.768 0 0121.485 12 59.77 59.77 0 013.27 20.876L5.999 12zm0 0h7.5" />
                            </svg>
                        </button>
                    </form>
                </div>
            ) : (
                <button
                    onClick={toggleChat}
                    className="bg-red-600 text-white p-4 rounded-full shadow-lg hover:bg-red-700 transition-all transform hover:scale-105"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                    </svg>
                </button>
            )}
        </div>
    );
};

export default ChatWidget;
