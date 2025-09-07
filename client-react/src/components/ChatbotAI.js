import React, { useState, useRef, useEffect, useContext } from "react";
import { MyUserContext } from "../reducers/MyUserReducer";
import botAvatar from "../asset/img/avatarChat.jpg";
import "../static/chatbotAI.css";

const API_BASE = process.env.REACT_APP_AI_BASE_URL || "http://localhost:5000";

export default function ChatbotAI() {
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [messages, setMessages] = useState([
        { role: "ai", text: "Xin chào 👋, mình là trợ lý ảo. Hãy đặt câu hỏi nhé!" },
    ]);
    const [input, setInput] = useState("");
    const [loading, setLoading] = useState(false);
    const bottomRef = useRef(null);

    const user = useContext(MyUserContext);
    const userAvatar = user?.avatar || "/default-avatar.png";
    const userName = user?.name || "Bạn";

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, loading]);

    async function sendMessage() {
        const question = input.trim();
        if (!question || loading) return;

        setMessages((prev) => [...prev, { role: "user", text: question }]);
        setInput("");
        setLoading(true);

        try {
            const res = await fetch(`${API_BASE}/query`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ question, top_k: 3 }),
            });

            if (!res.ok) throw new Error(await res.text());
            const data = await res.json();
            const answer = data?.answer ?? "Không có câu trả lời.";
            setMessages((prev) => [...prev, { role: "ai", text: answer }]);
        } catch (err) {
            console.error(err);
            setMessages((prev) => [...prev, { role: "ai", text: "⚠️ Có lỗi khi gọi AI-service." }]);
        } finally {
            setLoading(false);
        }
    }

    function onKeyDown(e) {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    }

    return (
        <div className="chatbot-container">
            {/* Icon nhỏ góc phải */}
            <img
                className={`avatar-chatbot ${!isDialogOpen ? "shake" : ""}`}
                src={botAvatar}
                alt="Chatbot Icon"
                onClick={() => setIsDialogOpen(true)}
            />

            {/* Dialog */}
            {isDialogOpen && (
                <div className="chatbot-dialog">
                    <div className="dialog-header">
                        <span>💬 Chat với trợ lý</span>
                        <button className="close-dialog" onClick={() => setIsDialogOpen(false)}>✖</button>
                    </div>

                    {/* Hộp chat */}
                    <div className="chat-box">
                        {messages.map((m, idx) => {
                            const isUser = m.role === "user";
                            const avatarSrc = isUser ? userAvatar : botAvatar;
                            const who = isUser ? userName : "AI";
                            return (
                                <div key={idx} className={`chat-row ${isUser ? "right" : "left"}`}>
                                    {!isUser && <img src={avatarSrc} alt={who} className="avatar" />}
                                    <div className={`bubble ${isUser ? "user" : "ai"}`}>
                                        <div className="name">{who}</div>
                                        <div className="text" style={{ whiteSpace: "pre-wrap" }}>{m.text}</div>
                                    </div>
                                    {isUser && <img src={avatarSrc} alt={who} className="avatar" />}
                                </div>
                            );
                        })}
                        {loading && <div className="typing">AI đang trả lời…</div>}
                        <div ref={bottomRef} />
                    </div>

                    {/* Ô nhập */}
                    <div className="input-row">
                        <textarea
                            rows={2}
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={onKeyDown}
                            placeholder="Nhập câu hỏi…"
                            className="chat-input"
                        />
                        <button
                            onClick={sendMessage}
                            disabled={loading || !input.trim()}
                            className="chat-btn"
                        >
                            Gửi
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
