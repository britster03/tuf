import matplotlib.pyplot as plt

# Read points from tsp300.txt
with open('tsp300.txt', 'r') as f:
    n = int(f.readline())
    points = [tuple(map(float, f.readline().split())) for _ in range(n)]

# Read solution from solution.txt
with open('solution.txt', 'r') as f:
    n_sol = int(f.readline())
    tour = list(map(int, f.read().split()))

# Extract coordinates in tour order
tour_points = [points[i] for i in tour]
# Add the starting point at the end to complete the tour
tour_points.append(tour_points[0])

# Separate x and y coordinates
x, y = zip(*tour_points)

# Plot the tour
plt.figure(figsize=(10, 10))
plt.plot(x, y, 'o-', markersize=3, linewidth=1, color='blue')
plt.title('TSP Tour Visualization')
plt.xlabel('X Coordinate')
plt.ylabel('Y Coordinate')
plt.grid(True)

# Save the plot to a file instead of showing it
plt.savefig('tsp_tour.png', dpi=300)

# Optionally, you can also save it as a PDF
# plt.savefig('tsp_tour.pdf')

# Close the plot to free memory
plt.close()

print("Tour plot has been saved as 'tsp_tour.png'.")
