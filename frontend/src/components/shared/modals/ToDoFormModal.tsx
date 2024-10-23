import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  Label,
  Input,
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
  Button,
  Popover,
  PopoverContent,
  PopoverTrigger,
  Calendar,
} from "@/components/ui-library";

import { useState } from "react";
import { Calendar as CalendarIcon } from "lucide-react";
import { format } from "date-fns";
import { cn } from "@/lib/utils";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { ToDoPayload } from "@/interfaces/ToDoPayload";
import { createTodo, updateTodo } from "@/services/ToDoService";
import { ToDo } from "@/interfaces/ToDo";
import { useTodoStore } from "@/hooks";
import { useToast } from "@/hooks/ui-library/use-toast";

const FormSchema = z.object({
  taskName: z
    .string()
    .min(1, { message: "Task Name is required." })
    .max(120, { message: "Task Name must not exceed 120 characters." }),
  priority: z.enum(["LOW", "MEDIUM", "HIGH"], {
    message: "Priority is required.",
  }),
  dueDate: z
    .date()
    .optional()
    .refine(
      (date) => {
        if (!date || date == undefined) return true;
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return date >= today;
      },
      { message: "Due Date cannot be earlier than today." }
    ),
});

const ToDoFormModal = ({
  onClose,
  todo,
  isEdit,
}: {
  onClose: () => void;
  todo?: ToDo;
  isEdit: boolean;
}) => {
  const [date, setDate] = useState<Date | undefined>(
    todo?.dueDate ? new Date(todo.dueDate + "T00:00:00") : undefined
  );
  const { fetchFilteredTodos } = useTodoStore();
  const { toast } = useToast();

  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
    defaultValues: {
      taskName: todo?.text || "",
      priority: todo?.priority || "LOW",
      dueDate: todo?.dueDate ? new Date(todo.dueDate + "T00:00:00") : undefined,
    },
  });

  const handleSubmit = async (data: z.infer<typeof FormSchema>) => {
    const toDoPayload: ToDoPayload = {
      text: data.taskName,
      priority: data.priority,
      dueDate: date ? format(date, "yyyy-MM-dd") : undefined,
    };

    const isDataChanged =
      toDoPayload.text !== todo?.text ||
      toDoPayload.priority !== todo?.priority ||
      toDoPayload.dueDate !== todo?.dueDate;

    if (isEdit && todo && !isDataChanged) {
      console.log("No changes detected. Closing modal.");
      onClose();
      return;
    }

    try {
      if (isEdit && todo) {
        await updateTodo(todo.id, toDoPayload);
      } else {
        await createTodo(toDoPayload);
      }

      await fetchFilteredTodos();
      toast({
        title: isEdit
          ? "Task Updated Successfully"
          : "Task Created Successfully",
      });
      onClose();
    } catch (error) {
      console.error(`Error ${isEdit ? "updating" : "creating"} ToDo:`, error);
    }
  };

  return (
    <Dialog open modal onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>
            {isEdit ? "Edit To Do" : "Create New To Do"}
          </DialogTitle>
          <DialogDescription>
            {isEdit
              ? "Edit the details of the task."
              : "Fill in the details for the new task."}
          </DialogDescription>
        </DialogHeader>

        <Label>Task Name</Label>
        <Input {...form.register("taskName")} placeholder="Task name" />
        {form.formState.errors.taskName && (
          <p className="text-red-500">
            {form.formState.errors.taskName.message}
          </p>
        )}

        <Label>Priority</Label>
        <Select
          defaultValue={form.getValues("priority")}
          onValueChange={(value) =>
            form.setValue("priority", value as "LOW" | "MEDIUM" | "HIGH")
          }
        >
          <SelectTrigger className="w-full">
            <SelectValue placeholder="Select" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="LOW">Low</SelectItem>
            <SelectItem value="MEDIUM">Medium</SelectItem>
            <SelectItem value="HIGH">High</SelectItem>
          </SelectContent>
        </Select>
        {form.formState.errors.priority && (
          <p className="text-red-500">
            {form.formState.errors.priority.message}
          </p>
        )}

        <Label className="mt-4">Due Date (Optional)</Label>
        <Popover>
          <PopoverTrigger asChild>
            <Button
              variant={"outline"}
              className={cn(
                "w-full justify-start text-left font-normal",
                !date && "text-muted-foreground"
              )}
            >
              <CalendarIcon className="mr-2 h-4 w-4" />
              {date ? format(date, "PPP") : <span>Pick a date</span>}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0">
            <Calendar
              mode="single"
              selected={date}
              onSelect={(selectedDate) => {
                setDate(selectedDate);
                form.setValue("dueDate", selectedDate || undefined);
              }}
              initialFocus
            />
          </PopoverContent>
        </Popover>
        {date && isEdit && (
          <Button
            onClick={() => {
              setDate(undefined);
              form.setValue("dueDate", undefined);
            }}
            variant="outline"
            className="mt-2"
          >
            Clear Date
          </Button>
        )}
        {form.formState.errors.dueDate && (
          <p className="text-red-500">
            {form.formState.errors.dueDate.message}
          </p>
        )}

        <DialogFooter>
          <Button onClick={form.handleSubmit(handleSubmit)}>
            {isEdit ? "Save Changes" : "Create"}
          </Button>
          <Button onClick={onClose} variant="secondary">
            Cancel
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default ToDoFormModal;
