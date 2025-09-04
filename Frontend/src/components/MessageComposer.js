import { trigger } from "polyrhythm";
import React, { useState } from "react";

const MessageComposer = () => {
  const [pendingMessage, setPendingMessage] = useState("");

  const handleChange = (e) => {
    setPendingMessage(e.target.value);
  };

  const handleSend = () => {
    setPendingMessage(""); // async - doesn't change pendingMessage
    trigger("message/create", { text: pendingMessage }); // <---- HERE!!
  };

  return (
    <div>
      <input value={pendingMessage} onchange={handleChange} />
      <button onclick={handleSend}>Send</button>
    </div>
  );
};
export default MessageComposer;