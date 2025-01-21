import { useState } from "react";
import {
  Label,
  Input,
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
  Button,
} from "@/components/ui-library";

import {
  FcHighPriority,
  FcLowPriority,
  FcMediumPriority,
} from "react-icons/fc";

import { GetTodosParams } from "@/interfaces/ToDoParams";
import useTodoStore from "@/hooks/useToDoStore";

const FilterSearch = () => {
  const [text, setText] = useState<string | undefined>(undefined);
  const [priority, setPriority] = useState<
    "HIGH" | "MEDIUM" | "LOW" | undefined
  >(undefined);
  const [done, setDone] = useState<boolean | undefined>(undefined);

  const { setParams, fetchFilteredTodos } = useTodoStore();

  const filterData = () => {
    const searchParams: GetTodosParams = {
      text,
      priority,
      done,
    };
    console.log("Filtering data with params:", searchParams);
    setParams(searchParams);
    fetchFilteredTodos();
  };

  const selectPriority = (value: string) => {
    if (["HIGH", "MEDIUM", "LOW"].includes(value)) {
      setPriority(value as "HIGH" | "MEDIUM" | "LOW");
    } else {
      setPriority(undefined);
    }
  };

  const selectState = (value: string) => {
    if (value === "done") {
      setDone(true);
    } else if (value === "undone") {
      setDone(false);
    } else {
      setDone(undefined);
    }
  };

  return (
    <div className="grid grid-cols-4 grid-rows-2 gap-4 p-4 rounded-lg border">
      {/* Filter By Name */}
      <div role="search" className="col-span-4 rounded-lg flex items-center space-x-4">
        <Label className="w-[85px]">Name</Label>
        <Input
          className="w-full"
          placeholder="Filter by task name..."
          value={text}
          onChange={(e) => setText(e.target.value)}
        />
      </div>

      {/* Filter By Priority */}
      <div className="col-span-2 rounded-lg flex items-center space-x-4">
        <Label className="w-[100px]">Priority</Label>
        <Select onValueChange={selectPriority}>
          <SelectTrigger className="w-full">
            <SelectValue placeholder="Select" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="undefined">All</SelectItem>
            <SelectItem value="LOW">
              <div className="flex items-center">
                <FcLowPriority className="mr-1 text-2xl" />
                Low
              </div>
            </SelectItem>
            <SelectItem value="MEDIUM">
              <div className="flex items-center">
                <FcMediumPriority className="mr-1 text-2xl" />
                Medium
              </div>
            </SelectItem>
            <SelectItem value="HIGH">
              <div className="flex items-center">
                <FcHighPriority className="mr-1 text-2xl" />
                High
              </div>
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Search button */}
      <div className="col-span-2 row-span-2 rounded-lg flex justify-end items-end p-2">
        <Button onClick={filterData}>Search</Button>
      </div>

      {/* Filter By Task Status */}
      <div className="col-span-2 rounded-lg flex items-center space-x-4">
        <Label className="w-[100px]">State</Label>
        <Select onValueChange={selectState}>
          <SelectTrigger className="w-full">
            <SelectValue placeholder="Select" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="undefined">All</SelectItem>
            <SelectItem value="done">Done</SelectItem>
            <SelectItem value="undone">Undone</SelectItem>
          </SelectContent>
        </Select>
      </div>
    </div>
  );
};

export default FilterSearch;
